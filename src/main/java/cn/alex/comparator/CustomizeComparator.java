package cn.alex.comparator;

import cn.alex.domain.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义多维度比较器
 * Created by WCY on 2022/9/28
 */
public class CustomizeComparator implements Comparator<Object> {

    private final List<CompareStrategy> compareStrategyList = new ArrayList<>();

    public CustomizeComparator append(CompareStrategy compareStrategy) {
        compareStrategyList.add(compareStrategy);
        return this;
    }

    @Override
    public int compare(Object forward, Object afterward) {
        int compare = 0; // 默认不排序
        for (CompareStrategy compareStrategy : compareStrategyList) {
            // 取数
            Function<Object, Object> customizeCompareValue = compareStrategy.getCustomizeCompareValue();
            Object forwardValue = null;
            Object afterwardValue = null;
            if (customizeCompareValue == null) {
                String name = compareStrategy.getName();
                forwardValue = getCompareValue(forward, name);
                afterwardValue = getCompareValue(afterward, name);
            } else {
                forwardValue = customizeCompareValue.apply(forward);
                afterwardValue = customizeCompareValue.apply(afterward);
            }

            // 比较
            if (forwardValue == null || afterwardValue == null) {
                compare = getNullCompare(forwardValue, afterwardValue);
            } else {
                Comparator comparator = compareStrategy.getComparator();
                if (comparator != null) {
                    compare = comparator.compare(forwardValue, afterwardValue);
                } else {
                    compare = compare(compareStrategy.getType(), forwardValue, afterwardValue);
                }
            }
            if (compare == 0) { // 下个维度继续排序
                continue;
            }
            return compareStrategy.isSort() ? compare : -compare;
        }
        return compare;
    }

    // sort true 升序, false 倒序
    private int getNullCompare(Object forwardValue, Object afterwardValue) {
        // compare = 1 forwardValue > afterwardValue
        // compare = 0 forwardValue = afterwardValue
        // compare = -1 forwardValue < afterwardValue
        int compare = 0;
        if (forwardValue == null) {
            compare = -1;
        } else if (afterwardValue == null) {
            compare = 1;
        }
        return compare;
    }

    private Object getCompareValue(Object target, String name) {
        Class<?> clazz = target.getClass();
        try {
            if (target instanceof Map) {
                Method method = clazz.getMethod("get", Object.class);
                return method.invoke(target, name);
            }
            // 属性一般都是私有的
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int compare(Class<?> type, Object forwardValue, Object afterwardValue) {
        if (type.equals(Integer.class)) {
            return Integer.compare((Integer) forwardValue, (Integer) afterwardValue);
        } else if (type.equals(Long.class)) {
            return Long.compare((Long) forwardValue, (Long) afterwardValue);
        } else if (type.equals(Double.class)) {
            return Double.compare((Double) forwardValue, (Double) afterwardValue);
        } else if (type.equals(BigDecimal.class)) {
            return ((BigDecimal) forwardValue).compareTo((BigDecimal) afterwardValue);
        } else if (type.equals(String.class)) {
            return ((String) forwardValue).compareTo((String) afterwardValue);
        }
        return 0;
    }

    public static void main(String[] args) {
        CustomizeComparator customizeComparator = new CustomizeComparator();

        Map<String, Object> target = new HashMap<>();
        target.put("name", "wcy");

        Object name = customizeComparator.getCompareValue(target, "name");
        System.out.println("name = " + name);

        User user = new User();
        user.setId(1L);
        Object id = customizeComparator.getCompareValue(user, "id");
        System.out.println("id = " + id);

        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 1);
        map1.put("name", "三国");
        map1.put("price", 18.8);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 2);
        map2.put("name", "红楼");
        map2.put("price", 14.8);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", 1);
        map3.put("name", "西游");
        map3.put("price", 16.8);
        mapList.add(map1);
        mapList.add(map2);
        mapList.add(map3);

        customizeComparator.append(new CompareStrategy().name("id").type(Integer.class));
        customizeComparator.append(new CompareStrategy().name("price").type(Double.class).sort(false));
        customizeComparator.append(new CompareStrategy().name("name").type(String.class));

        List<Map<String, Object>> collectList = mapList.stream().sorted(customizeComparator).collect(Collectors.toList());
        collectList.forEach(System.out::println);

        List<Integer> numList = Arrays.asList(null, -1, 7, null);
        List<Integer> collect = numList.stream().sorted((n1, n2) -> {
            if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            }
            return Integer.compare(n1, n2);
        }).collect(Collectors.toList());
        System.out.println("collect = " + collect);

        customizeComparator = new CustomizeComparator().append(
                new CompareStrategy().type(Double.class)
                        .customizeCompareValue(value -> ((List<Map<String, Object>>) value).get(0).get("price"))
                        .sort(false));

        List<List<Map<String, Object>>> dataList = new ArrayList<>();
        List<Map<String, Object>> data1 = new ArrayList<>();
        data1.add(map1);
        data1.add(map2);
        List<Map<String, Object>> data2 = new ArrayList<>();
        data2.add(map3);
        dataList.add(data1);
        dataList.add(data2);
        dataList.sort(customizeComparator);
        dataList.forEach(System.out::println);
    }

}
