package cn.alex.util.resource;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by WCY on 2021/9/17
 */
public class ResourceUtil {

    public static void main(String[] args) throws IOException {
        Properties properties = readResource();
        ByteArrayOutputStream outputStream = getOutputStream("c.txt");
        System.out.println(outputStream);
    }

    /*
        一 java读取properties文件中文乱码
        问题分析, 1 编码问题(排除), 2 字节流无法读取中文, 所以要转换为字符流

        二 Class getResourceAsStream和ClassLoader getResourceAsStream区别
        1 Class getResourceAsStream和ClassLoader getResourceAsStream本质一样, 都是使用ClassLoader.getResource加载资源
        2 Class.getResource真正调用ClassLoader.getResource方法之前, 会先获取文件的路径执行resolveName(name)
        3 path以'/'开头时, 则是从项目的ClassPath根下获取资源, path不以'/'开头时, 有两种情况, 第一种情况是调用的类名中包含点(.),
        是全限定类名, 默认是从此类所在的包下取资源, 第二种情况和path以'/'开头时一样从项目的ClassPath根下获取资源
     */
    public static Properties readResource() throws IOException {
        //InputStream resource = ResourceUtil.class.getResourceAsStream("/name.properties");
        InputStream resource = ResourceUtil.class.getClassLoader().getResourceAsStream("name.properties");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
        Properties properties = new Properties();
        // 读取文件
        properties.load(reader);
        properties.put("name", "wcy");
        properties.put("age", "18");
        properties.setProperty("address", "china");

        // 保存文件
        properties.store(new FileOutputStream(getClassPath("api") + "name2.properties"), "remark");
        return properties;
    }

    /**
     * 读取文件流
     * @param path 路径
     * @return 输出流
     */
    public static ByteArrayOutputStream getOutputStream(String path) {
        int len;
        byte[] bs = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (InputStream inputStream = ResourceUtil.class.getClassLoader().getResourceAsStream(path)) {
            while ((len = inputStream.read(bs)) != -1) {
                bos.write(bs, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos;
    }

    /**
     * 打印字节数组
     * @param value 值
     */
    public static void printByte(Object value) {
        if (value instanceof byte[]) { // 直接打印字节数组
            System.out.println(Arrays.toString((byte[]) value));
        } else if (value instanceof String) { // value 文件路径, 打印文件中字节数组
            ByteArrayOutputStream bos = ResourceUtil.getOutputStream((String) value);
            System.out.println(Arrays.toString(bos.toByteArray()));
        }
    }

    /**
     * 获取本地类路径 (@Test不需要设置parent名称, main方法需要设置)
     * @param parent 模块目录
     * @param path   文件路径
     * @return 本地类路径
     */
    public static String getClassPath(String parent, String path) {
        StringBuilder sb = new StringBuilder(System.getProperty("user.dir"));
        if (parent != null && !"".equals(parent)) {
            sb.append("\\").append(parent);
        }
        sb.append("\\src\\main\\resources\\");
        if (path != null && !"".equals(path)) {
            sb.append(path);
        }
        return sb.toString();
    }

    public static String getClassPath(String parent) {
        return getClassPath(parent, null);
    }

    public static String getClassPath() {
        return getClassPath(null, null);
    }
}
