import cn.alex.comparator.CompareStrategy;
import cn.alex.comparator.CustomizeComparator;
import cn.alex.domain.User;

import java.text.Collator;
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
        //String[] arrStrings = {"乔峰", "郭靖", "杨过", "张无忌","韦小宝"};
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "乔过你");
        map1.put("type", 0);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "郭靖");
        map2.put("type", 2);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("name", "乔过唉");
        map3.put("type", 2);
        Map<String, Object> map4 = new HashMap<>();
        map4.put("name", "张无忌");
        map4.put("type", 2);
        Map<String, Object> map5 = new HashMap<>();
        map5.put("name", "韦小宝");
        map5.put("type", 2);
        List<Map<String, Object>> nameList = Arrays.asList(map1, map2, map3, map4, map5);

        // Collator类是用来执行区分语言环境的 String 比较的, 这里选择使用CHINA
        Collator comparator = Collator.getInstance(Locale.CHINA);
        CustomizeComparator customizeComparator = new CustomizeComparator();
        customizeComparator.append(new CompareStrategy().name("type").type(Integer.class));
        customizeComparator.append(new CompareStrategy().name("name").comparator(comparator));

        // 使根据指定比较器产生的顺序对指定对象数组进行排序
        List<Map<String, Object>> collectList = nameList.stream().sorted(customizeComparator).collect(Collectors.toList());
        collectList.forEach(System.out::println);

        User user1 = new User(1L, "张三", 12, "126");
        User user2 = new User(3L, "李四", 18, "163");
        User user3 = new User(4L, "王五", 15, "110");
        User user4 = new User(2L, "赵六", 16, "120");
        List<User> userList = Arrays.asList(user1, user2, user3, user4);
        userList.forEach(System.out::println);
        System.out.println("----------------");
        customizeComparator = new CustomizeComparator();
        customizeComparator.append(new CompareStrategy().name("id").type(Long.class));
        customizeComparator.append(new CompareStrategy().name("name").comparator(comparator));
        customizeComparator.append(new CompareStrategy().name("age").type(Integer.class));
        customizeComparator.append(new CompareStrategy().name("email").type(String.class));
        List<User> collect = userList.stream().sorted(customizeComparator).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }

}
