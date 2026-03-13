package cn.alex.util.encrtpt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

public class AesUtil {

    /**
     * 生成16位不重复的随机密钥，含数字+大小写
     * @return 随机密钥
     */
    public static String getGUID() {
        StringBuilder uid = new StringBuilder();
        // 产生16位的强随机数
        Random rd = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            // 产生0-2的3位随机数
            int type = rd.nextInt(3);
            switch (type) {
                case 0:
                    // 0-9的随机数
                    uid.append(rd.nextInt(10));
                    break;
                case 1:
                    // ASCII在65-90之间为大写,获取大写随机
                    uid.append((char) (rd.nextInt(25) + 65));
                    break;
                case 2:
                    // ASCII在97-122之间为小写，获取小写随机
                    uid.append((char) (rd.nextInt(25) + 97));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }

    /**
     * 算法
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    private static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception 抛出异常
     */
    private static byte[] base64Decode(String base64Code) throws Exception {
        return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }

    /**
     * AES加密
     * @param content 待加密的内容
     * @param key     加密密钥
     * @return 加密后的byte[]
     */
    private static byte[] aesEncryptToBytes(String content, String key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param key     加密密钥
     * @return 加密后的base 64 code
     */
    public static String encode(String content, String key) throws Exception {
        return base64Encode(aesEncryptToBytes(content, key));
    }

    /**
     * AES解密
     * @param encryptBytes 待解密的byte[]
     * @param key          解密密钥
     * @return 解密后的String
     */
    private static String aesDecryptByBytes(byte[] encryptBytes, String key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @param key        解密密钥
     * @return 解密后的string
     */
    public static String decode(String encryptStr, String key) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), key);
    }

    public static void main(String[] args) throws Exception {
        String content = "{\"bank_identid\":2755,\"bank_product_name\":\"经营随e贷\",\"bankquota\":100.5,\"borrotime\":24,\"createtime\":\"2021-02-13 00:00:00.0\",\"credit_code\":\"WD20210123123456\",\"finanid\":1623,\"identid\":4,\"loan_code\":\"JT20210123123456\",\"loan_contract\":\"HT20210123123456\",\"loantime\":\"2021-02-15 00:00:00.0\",\"repaytime\":\"2023-02-15 00:00:00.0\",\"totalrate\":5.1}";
        System.out.println("加密前：" + content);
        String key = getGUID();
        System.out.println("加密密钥和解密密钥：" + key);
        String encrypt = encode(content, key);
        System.out.println("加密后：" + encrypt);
        String decrypt = decode(encrypt, key);
        System.out.println("解密后：" + decrypt);
    }

}
