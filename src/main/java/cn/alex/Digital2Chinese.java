package cn.alex;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WCY on 2022/9/22
 */
public class Digital2Chinese {

    // 亿亿级别
    public static void main(String[] args) {
        // 十万四千零三十二
        String digital = "104032";
        //digital = "104032888";
        //digital = "101000100000011100";
        //digital = "10001000008";
        //digital = "11000000000";
        //digital = "100010101111";
        //digital = "101000010001001";
        digital = "11";
        System.out.println(digital + " " + numToCharacter(digital.toCharArray()));
    }

    // 亿内十进, 亿上万进 (兆: 万亿, 京: 万万亿)
    static String[] tenUnits = new String[]{"", "十", "百", "千"};
    static String[] tenThousandUnits = new String[]{"", "万", "亿", "兆", "京"};
    static Map<Character, Character> digitalMap = new HashMap<>();

    static {
        digitalMap.put('0', '零');
        digitalMap.put('1', '一');
        digitalMap.put('2', '二');
        digitalMap.put('3', '三');
        digitalMap.put('4', '四');
        digitalMap.put('5', '五');
        digitalMap.put('6', '六');
        digitalMap.put('7', '七');
        digitalMap.put('8', '八');
        digitalMap.put('9', '九');
    }

    /**
     * 数字转字符串
     * @param values 值
     * @return 文字字符串
     */
    static String numToCharacter(char[] values) {
        StringBuilder result = new StringBuilder();
        int len = values.length;
        int tenThousandIndex = (len - 1) / 4; // 单位下标
        int tempLen = len % 4 == 0 ? 4 : len % 4; // 第一次截取数量

        StringBuilder buffer = new StringBuilder();
        for (char character : values) {
            buffer.append(character);
            if (buffer.length() == tempLen) {
                numToCharacter(result, buffer.toString(), tenThousandUnits[tenThousandIndex--]);
                buffer = new StringBuilder(); // 清空buffer
                tempLen = 4; // 非第一次后面都是4
            }
        }
        return result.toString();
    }

    /**
     * 数字转字符串
     * @param result 最终结果
     * @param value  当前值
     * @param unit   单位
     */
    static void numToCharacter(StringBuilder result, String value, String unit) {
        if (!value.equals("0000")) {
            StringBuilder temp = new StringBuilder();
            char[] chars = value.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                Character character = chars[i];
                Character chinese = digitalMap.get(character);
                if (character.equals('0')) {
                    if (isFillZero(temp, value, i)) {
                        temp.append(chinese);
                    }
                } else {
                    // 优化 10 - 19 情况
                    if (value.length() != 2 || !character.equals('1') || i != 0) {
                        temp.append(chinese);
                    }
                    temp.append(tenUnits[chars.length - i - 1]);
                }
            }
            result.append(temp).append(unit);
        } else {
            // TODO 最后0000是否填充零? 如果要填充的话, 需要判断后续是否还有不为零的数字, 还有最终结果最后位之前是否填充过零
        }
    }

    /**
     * 是否填充零
     * @param temp   当前结果
     * @param buffer 4位缓存
     * @param index  0到3
     * @return 是否填充
     */
    static boolean isFillZero(StringBuilder temp, String buffer, int index) {
        // 以零开始的第一个零 0100 0010 0001 0110 0101 0011 0111
        if (buffer.startsWith("0") && index == 0) {
            return true;
        }

        // 以'000'或'00'结尾或最后一位'0' 1000 0100 1100 0010 0110 1010 1110
        if (buffer.endsWith("000") || buffer.endsWith("00") || index == buffer.length() - 1) {
            return false;
        }

        // 当前结果最后一位不是零 0001 1001 0011
        return temp.lastIndexOf("零") != temp.length() - 1;
    }
}
