package cn.alex.util.beanutil;

/**
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class JavaBeansUtil {
    public JavaBeansUtil() {
    }

    public static String getGetterMethodName(String property, String javaType) {
        StringBuilder sb = covertMethodPost(property);
        if ("boolean".equals(javaType)) {
            sb.insert(0, "is");
        } else {
            sb.insert(0, "get");
        }
        return sb.toString();
    }

    public static String getSetterMethodName(String property) {
        StringBuilder sb = covertMethodPost(property);
        sb.insert(0, "set");
        return sb.toString();
    }

    private static StringBuilder covertMethodPost(String property) {
        StringBuilder sb = new StringBuilder();
        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0)) && (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1)))) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        return sb;
    }
}
