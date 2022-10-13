package cn.alex.pagination;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by WCY on 2022/10/12
 */
public class PaginationInCode {
    public static void main(String[] args) {
        // 保证 pageNum >= 1
        int pageNum = 2;
        int pageSize = 3;

        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 2);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", 3);
        Map<String, Object> map4 = new HashMap<>();
        map4.put("id", 4);
        Map<String, Object> map5 = new HashMap<>();
        map5.put("id", 5);
        List<Map<String, Object>> dataList = Arrays.asList(map1, map2, map3, map4, map5);

        if (dataList.size() > pageSize) {
            dataList = dataList.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        }

        dataList.forEach(System.out::println);
    }
}
