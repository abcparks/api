package cn.alex.util.comparator;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by WCY on 2022/9/28
 */
@Data
@Accessors(chain = true, fluent = true)
public class CompareStrategy {

    /**
     * 排序
     * true 升序
     * false 降序
     */
    private boolean sort = true;

    /**
     * null值排序
     * true nullsFirst
     * false nullsLast
     */
    private boolean nullsSort = true;

    /**
     * 动态获取值(禁用则直接比较, 无需获取比较值)
     */
    private boolean dynamicValue = true;

    /**
     * 比较器
     */
    private Comparator<Object> comparator;

    /**
     * 自定义对象(用于获取比较值)
     */
    private Object compareObject;

    /**
     * 自定义比较值(优先级高于compareObject获取的比较值)
     */
    private Function<Object, Object> customizeCompareValue;

}
