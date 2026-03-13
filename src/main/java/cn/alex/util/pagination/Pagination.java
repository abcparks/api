package cn.alex.util.pagination;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by WCY on 2022/10/12
 */
@Getter
public class Pagination<T> {

    private long total;

    private List<T> dataList;

    public Pagination(List<T> dataList) {
        this.dataList = dataList;
    }

    public Pagination<T> startSort(Comparator<T> comparator) {
        if (comparator != null) {
            dataList.sort(comparator);
        }
        return this;
    }

    public Pagination<T> startPage(long pageNum, long pageSize) {
        this.total = dataList.size();
        if (dataList.size() > pageSize) {
            dataList = dataList.stream().skip((pageNum < 1 ? 0 : pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        }
        return this;
    }

    public static void main(String[] args) {
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

        // 保证 pageNum >= 1
        int pageNum = 2;
        int pageSize = 3;
        Pagination<Map<String, Object>> pagination = new Pagination<>(dataList).startPage(pageNum, pageSize);
        pagination.getDataList().forEach(System.out::println);
    }

}
