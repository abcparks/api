package cn.alex.percent;

import java.util.Arrays;

/**
 * Created by WCY on 2022/9/29
 */
public class FixedPercent {

    public static int[] getPercentValue(int[] arr) {
        double sum = Arrays.stream(arr).sum();

        double digits = 100;
        double[] votesPerQuota = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            double val = arr[i] / sum * digits;
            votesPerQuota[i] = val;
        }

        // 再向下取值, 组成数组
        int[] seats = new int[arr.length];
        for (int i = 0; i < votesPerQuota.length; i++) {
            seats[i] = (int) Math.floor(votesPerQuota[i]);
        }

        // 再新计算合计, 用于判断与总数量是否相同,相同则占比会100%
        double currentSum = 0;
        for (double seat : seats) {
            currentSum += seat;
        }
        // 余数部分的数组: 原先数组减去向下取值的数组, 得到余数部分的数组
        double[] remainder = new double[arr.length];
        for (int i = 0; i < seats.length; i++) {
            remainder[i] = votesPerQuota[i] - seats[i];
        }

        while (currentSum < digits) {
            double max = 0;
            int maxId = 0;
            for (int i = 0; i < remainder.length; i++) {
                if (remainder[i] > max) {
                    max = remainder[i];
                    maxId = i;
                }
            }
            // 对最大项余额加1
            seats[maxId]++;
            // 已经增加最大余数加1, 则下次判断就可以不需要再判断这个余额数
            remainder[maxId] = 0;
            // 总的也要加1, 为了判断是否总数是否相同, 跳出循环
            currentSum++;
        }
        return seats;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{31, 31, 31, 31, 31, 31};
        int[] percentValue = getPercentValue(arr);
        for (int v : percentValue) {
            System.out.println(v);
        }
    }

}
