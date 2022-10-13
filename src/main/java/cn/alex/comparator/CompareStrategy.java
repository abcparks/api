package cn.alex.comparator;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by WCY on 2022/9/28
 */
@Getter
@Setter
public class CompareStrategy {

    // sort true 升序 false 降序
    private boolean sort = true;

    // 定义取数类型
    private Class<?> type;

    // 定义取数策略
    private String name;

    private Function<Object, Object> customizeCompareValue;

    private Comparator<?> comparator;

    public CompareStrategy sort(boolean sort) {
        this.sort = sort;
        return this;
    }

    public CompareStrategy type(Class<?> type) {
        this.type = type;
        return this;
    }

    public CompareStrategy name(String name) {
        this.name = name;
        return this;
    }

    public CompareStrategy customizeCompareValue(Function<Object, Object> customizeCompareValue) {
        this.customizeCompareValue = customizeCompareValue;
        return this;
    }

    public CompareStrategy comparator(Comparator<?> comparator) {
        this.comparator = comparator;
        return this;
    }
}
