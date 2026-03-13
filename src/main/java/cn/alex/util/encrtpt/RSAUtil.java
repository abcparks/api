package cn.alex.util.encrtpt;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 2019-07-23
 * RSA加解密工具类
 */
public class RSAUtil {
    protected static final Log log = LogFactory.getLog(RSAUtil.class);
    private static final String KEY_RSA_TYPE = "RSA";
    // JDK方式RSA加密最大只有1024位
    private static final int KEY_SIZE = 1024;
    private static final int ENCODE_PART_SIZE = KEY_SIZE / 8;
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String PUBLIC_KEY_NAME = "public";
    public static final String PRIVATE_KEY_NAME = "private";

    // RSA公钥
    private static String privateKeyBase64Str = null;
    private static String publicKeyBase64Str = null;

    static {
        Map<String, String> rsaKeys = createRSAKeys();
        privateKeyBase64Str = rsaKeys.get(PRIVATE_KEY_NAME);
        publicKeyBase64Str = rsaKeys.get(PUBLIC_KEY_NAME);
    }

    /**
     * 创建公钥秘钥
     * @return 公钥私钥
     */
    public static Map<String, String> createRSAKeys() {
        // 存放公私秘钥的Base64位加密
        Map<String, String> keyPairMap = new HashMap<>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_RSA_TYPE);
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 获取公钥秘钥
            String publicKeyValue = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
            String privateKeyValue = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());

            // 存入公钥秘钥, 以便以后获取
            keyPairMap.put(PUBLIC_KEY_NAME, publicKeyValue);
            keyPairMap.put(PRIVATE_KEY_NAME, privateKeyValue);
        } catch (NoSuchAlgorithmException e) {
            log.error("当前JDK版本没找到RSA加密算法！");
            e.printStackTrace();
        }
        return keyPairMap;
    }

    /**
     * 公钥加密
     * 描述：
     * 1字节 = 8位
     * 最大加密长度如 1024位私钥时, 最大加密长度为 128-11 = 117字节, 不管多长数据, 加密出来都是128字节长度。
     * @param content 加密内容
     * @return 加密结果
     */
    public static String encode(String content) {
        // RSA加密公钥
        byte[] publicBytes = Base64.decodeBase64(publicKeyBase64Str);
        //公钥加密
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicBytes);
        List<byte[]> alreadyEncodeListData = new LinkedList<>();

        int maxEncodeSize = ENCODE_PART_SIZE - 11;
        String encodeBase64Result = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_RSA_TYPE);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(KEY_RSA_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] sourceBytes = content.getBytes(StandardCharsets.UTF_8);
            int sourceLen = sourceBytes.length;
            for (int i = 0; i < sourceLen; i += maxEncodeSize) {
                int curPosition = sourceLen - i;
                int tempLen = curPosition;
                if (curPosition > maxEncodeSize) {
                    tempLen = maxEncodeSize;
                }
                // 待加密分段数据
                byte[] tempBytes = new byte[tempLen];
                System.arraycopy(sourceBytes, i, tempBytes, 0, tempLen);
                byte[] tempAlreadyEncodeData = cipher.doFinal(tempBytes);
                alreadyEncodeListData.add(tempAlreadyEncodeData);
            }
            // 加密次数
            int partLen = alreadyEncodeListData.size();

            int allEncodeLen = partLen * ENCODE_PART_SIZE;
            // 存放所有RSA分段加密数据
            byte[] encodeData = new byte[allEncodeLen];
            for (int i = 0; i < partLen; i++) {
                byte[] tempByteList = alreadyEncodeListData.get(i);
                System.arraycopy(tempByteList, 0, encodeData, i * ENCODE_PART_SIZE, ENCODE_PART_SIZE);
            }
            encodeBase64Result = Base64.encodeBase64String(encodeData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeBase64Result;
    }

    /**
     * 私钥解密
     * @param content 解密内容
     * @return 解密结果
     */
    public static String decode(String content) {
        try {
            // 换传值过程中丢失的字符串
            content = content.replace(" ", "+");
            // RSA加密私钥
            //String privateKeyBase64Str = ConfigFileUtil.get("privateKey");
            RSAPrivateKey privateKey = getPrivateKey(privateKeyBase64Str);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(content),
                    privateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + content + "]时遇到异常", e);
        }
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串(经过base64编码)
     */
    public static RSAPrivateKey getPrivateKey(String privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] data, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (data.length > offSet) {
                if (data.length - offSet > maxBlock) {
                    buff = cipher.doFinal(data, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(data, offSet, data.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultData = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultData;
    }

    public static void main(String[] args) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("channelUid", "91320191062611800K");
            JSONObject userInfo = new JSONObject();
            userInfo.put("idcard", "320811198905213079");
            userInfo.put("name", "企业");
            userInfo.put("mobile", "17327883052");
            JSONObject legalInfo = new JSONObject();
            legalInfo.put("idcard", "320111198208253222");
            legalInfo.put("name", "许南芳");
            legalInfo.put("mobile", "17301593326");
            JSONObject companyInfo = new JSONObject();
            companyInfo.put("companyName", "南京中科软智信息技术有限公司");
            companyInfo.put("companyCode", "91320191062611800K");
            jsonObj.put("userInfo", userInfo);
            jsonObj.put("legalInfo", legalInfo);
            jsonObj.put("companyInfo", companyInfo);
            jsonObj.put("deviceId", "");
            String encodeStr = RSAUtil.encode(jsonObj.toJSONString());
            System.out.println(encodeStr);
            String decodeStr = RSAUtil.decode(encodeStr);
            System.out.println("decodeStr = " + decodeStr);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("加解密异常");
        }
    }
}
