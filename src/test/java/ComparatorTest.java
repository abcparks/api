import cn.alex.domain.User;
import cn.alex.util.comparator.CompareStrategy;
import cn.alex.util.comparator.CustomizeComparator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by WCY on 2022/3/21
 */
public class ComparatorTest {

    /*
     * java实现按中文首字母排序的方式
     */
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

        customizeComparator.append(new CompareStrategy().compareObject("id"));
        customizeComparator.append(new CompareStrategy().compareObject("price").sort(false));
        customizeComparator.append(new CompareStrategy().compareObject("name"));

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
                new CompareStrategy().customizeCompareValue(value -> ((List<Map<String, Object>>) value).get(0).get("price"))
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
