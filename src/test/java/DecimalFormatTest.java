import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * DecimalFormat 最全格式化案例
 * 覆盖：基础补零、千分位、单位、百分比、本地化、科学计数法、自定义符号、特殊数值
 */
public class DecimalFormatTest {

    public static void main(String[] args) {
        // ==================== 基础场景 ====================
        System.out.println("===== 1. 基础小数位控制 =====");
        // 1.1 固定小数位（不足补0）：0是强制占位符
        DecimalFormat df1 = new DecimalFormat("0.000");
        System.out.println("12.3 → " + df1.format(new BigDecimal("12.3"))); // 12.300
        System.out.println("12 → " + df1.format(new BigDecimal("12")));     // 12.000

        // 1.2 可选小数位（无值不显示）：#是可选占位符
        DecimalFormat df2 = new DecimalFormat("#.###");
        System.out.println("12.3 → " + df2.format(new BigDecimal("12.3"))); // 12.3
        System.out.println("12 → " + df2.format(new BigDecimal("12")));     // 12

        // ==================== 千分位分隔符 ====================
        System.out.println("\n===== 2. 千分位分隔符 =====");
        // 2.1 基础千分位 + 2位小数
        DecimalFormat df3 = new DecimalFormat("#,##0.00");
        System.out.println("1234567.89 → " + df3.format(new BigDecimal("1234567.89"))); // 1,234,567.89

        // 2.2 整数部分千分位（无小数）
        DecimalFormat df4 = new DecimalFormat("#,##0");
        System.out.println("9876543 → " + df4.format(new BigDecimal("9876543"))); // 9,876,543

        // ==================== 单位拼接 ====================
        System.out.println("\n===== 3. 数值+单位拼接 =====");
        // 3.1 后缀单位（金额、重量）
        DecimalFormat df5 = new DecimalFormat("#,##0.00 元");
        System.out.println("1234.5 → " + df5.format(new BigDecimal("1234.5"))); // 1,234.50 元

        // 3.2 前缀单位（货币符号）
        DecimalFormat df6 = new DecimalFormat("¥#,##0.00");
        System.out.println("6789.123 → " + df6.format(new BigDecimal("6789.123"))); // ¥6,789.12

        // ==================== 百分比格式化 ====================
        System.out.println("\n===== 4. 百分比格式化 =====");
        DecimalFormat df7 = new DecimalFormat("0.00%"); // 自动×100 + 百分号
        System.out.println("0.1234 → " + df7.format(new BigDecimal("0.1234"))); // 12.34%
        System.out.println("1.2 → " + df7.format(new BigDecimal("1.2")));       // 120.00%

        // ==================== 整数位补零（固定长度） ====================
        System.out.println("\n===== 5. 整数位固定长度（补零） =====");
        DecimalFormat df8 = new DecimalFormat("00000.00"); // 整数位固定5位，不足补0
        System.out.println("123.45 → " + df8.format(new BigDecimal("123.45"))); // 00123.45
        System.out.println("7.8 → " + df8.format(new BigDecimal("7.8")));       // 00007.80

        // ==================== 本地化格式（不同地区） ====================
        System.out.println("\n===== 6. 本地化格式化 =====");
        BigDecimal num = new BigDecimal("123456.78");
        // 6.1 美国格式（逗号分隔千分位，点分隔小数）
        DecimalFormat usDf = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        usDf.applyPattern("#,##0.00");
        System.out.println("美国格式：" + usDf.format(num)); // 123,456.78

        // 6.2 德国格式（点分隔千分位，逗号分隔小数）
        DecimalFormat deDf = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);
        deDf.applyPattern("#,##0.00");
        System.out.println("德国格式：" + deDf.format(num)); // 123.456,78

        // ==================== 自定义分隔符 ====================
        System.out.println("\n===== 7. 自定义数字分隔符 =====");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('|'); // 自定义小数分隔符为|
        symbols.setGroupingSeparator('^'); // 自定义千分位分隔符为^
        DecimalFormat df9 = new DecimalFormat("#,##0.00", symbols);
        System.out.println("12345.67 → " + df9.format(new BigDecimal("12345.67"))); // 12^345|67

        // ==================== 科学计数法（按需切换） ====================
        System.out.println("\n===== 8. 科学计数法控制 =====");
        BigDecimal bigNum = new BigDecimal("123456789012345.67");
        // 8.1 启用科学计数法
        DecimalFormat df10 = new DecimalFormat("0.00E00");
        System.out.println("科学计数法：" + df10.format(bigNum)); // 1.23E14

        // 8.2 禁用科学计数法（大数值也正常显示）
        DecimalFormat df11 = new DecimalFormat("#,##0.00");
        df11.setMaximumIntegerDigits(Integer.MAX_VALUE); // 不限制整数位长度
        System.out.println("禁用科学计数法：" + df11.format(bigNum)); // 123,456,789,012,345.67

        // ==================== 特殊数值处理 ====================
        System.out.println("\n===== 9. 零值/空值/负数处理 =====");
        // 9.1 零值处理
        DecimalFormat df12 = new DecimalFormat("0.00 元");
        System.out.println("零值：" + df12.format(BigDecimal.ZERO)); // 0.00 元

        // 9.2 空值处理（实际开发必须判空）
        BigDecimal nullNum = null;
        String nullResult = nullNum == null ? "0.00 元" : df12.format(nullNum);
        System.out.println("空值兜底：" + nullResult); // 0.00 元

        // 9.3 负数处理（自定义负数格式）
        DecimalFormat df13 = new DecimalFormat("¤#,##0.00;¤-#,##0.00"); // 正数/负数分开定义
        System.out.println("负数：" + df13.format(new BigDecimal("-1234.56"))); // ¥-1,234.56
    }

}