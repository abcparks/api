package cn.alex.util.comparator;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 自定义多维度比较器
 * Created by WCY on 2025/11/13
 */
public class CustomizeComparator implements Comparator<Object> {

    // 排序策略集合
    private final List<CompareStrategy> compareStrategyList = new ArrayList<>();

    // 追加排序策略
    public CustomizeComparator append(CompareStrategy compareStrategy) {
        compareStrategyList.add(compareStrategy);
        return this;
    }

    // 缓存反射方法与属性
    private static final String DELIMITER = "#";
    private static final String FIELD_DELIMITER = ":";
    private final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    private final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    private Method getCacheMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        // 构建缓存key
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName()).append(DELIMITER).append(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                sb.append(FIELD_DELIMITER).append(parameterType.getName());
            }
        }

        // 获取并添加缓存
        String key = sb.toString();
        return METHOD_CACHE.computeIfAbsent(key, k -> {
            try {
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Field getCacheField(Class<?> clazz, Object fieldName) {
        // 构建缓存key
        String key = clazz.getName() + DELIMITER + fieldName;
        // 获取并添加缓存
        return FIELD_CACHE.computeIfAbsent(key, k -> {
            try {
                Field field = clazz.getDeclaredField(String.valueOf(fieldName));
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 反射方法名 map.get, customize.apply
    private static final String MAP_METHOD_NAME = "get";
    private static final String CUSTOMIZE_OBJECT_METHOD_NAME = "apply";

    @Override
    @SneakyThrows
    public int compare(Object forward, Object afterward) {
        // 默认不排序
        int compare = 0;
        for (CompareStrategy compareStrategy : compareStrategyList) {
            // 取数
            Object forwardValue = forward;
            Object afterwardValue = afterward;
            if (compareStrategy.dynamicValue()) {
                Function<Object, Object> customizeCompareValue = compareStrategy.customizeCompareValue();
                Object compareObject = compareStrategy.compareObject();
                if (customizeCompareValue != null) {
                    forwardValue = customizeCompareValue.apply(forward);
                    afterwardValue = customizeCompareValue.apply(afterward);
                } else if (compareObject != null) {
                    forwardValue = this.getCompareValue(forward, compareObject);
                    afterwardValue = this.getCompareValue(afterward, compareObject);
                } else {
                    throw new RuntimeException("比较值不存在");
                }
            }
            // 比较
            if (forwardValue == null || afterwardValue == null) {
                compare = this.nullsCompare(compareStrategy.nullsSort(), forwardValue, afterwardValue);
            } else {
                // 存在比较器优先使用比较器, 不存在则进行数值比较
                Comparator<Object> comparator = compareStrategy.comparator();
                if (comparator != null) {
                    compare = comparator.compare(forwardValue, afterwardValue);
                } else {
                    compare = this.valuesCompare(forwardValue, afterwardValue);
                }
            }
            // 继续比较
            if (compare == 0) {
                continue;
            }
            return compareStrategy.sort() ? compare : -compare;
        }
        return compare;
    }

    private int nullsCompare(boolean nullsSort, Object forwardValue, Object afterwardValue) {
        // compare = 1 forwardValue > afterwardValue
        // compare = 0 forwardValue = afterwardValue
        // compare = -1 forwardValue < afterwardValue
        int compare = 0;
        if (forwardValue == null && afterwardValue == null) {
            return compare;
        } else if (forwardValue == null) {
            compare = -1;
        } else if (afterwardValue == null) {
            compare = 1;
        }
        return nullsSort ? compare : -compare;
    }

    private int valuesCompare(Object forwardValue, Object afterwardValue) {
        if (forwardValue instanceof Byte && afterwardValue instanceof Byte) {
            return Byte.compare((Byte) forwardValue, (Byte) afterwardValue);
        } else if (forwardValue instanceof Short && afterwardValue instanceof Short) {
            return Short.compare((Short) forwardValue, (Short) afterwardValue);
        } else if (forwardValue instanceof Integer && afterwardValue instanceof Integer) {
            return Integer.compare((Integer) forwardValue, (Integer) afterwardValue);
        } else if (forwardValue instanceof Long && afterwardValue instanceof Long) {
            return Long.compare((Long) forwardValue, (Long) afterwardValue);
        } else if (forwardValue instanceof Float && afterwardValue instanceof Float) {
            return Float.compare((Float) forwardValue, (Float) afterwardValue);
        } else if (forwardValue instanceof Double && afterwardValue instanceof Double) {
            return Double.compare((Double) forwardValue, (Double) afterwardValue);
        } else if (forwardValue instanceof Character && afterwardValue instanceof Character) {
            return Character.compare((Character) forwardValue, (Character) afterwardValue);
        } else if (forwardValue instanceof String && afterwardValue instanceof String) {
            return ((String) forwardValue).compareTo((String) afterwardValue);
        } else if (forwardValue instanceof Boolean && afterwardValue instanceof Boolean) {
            return ((Boolean) forwardValue).compareTo((Boolean) afterwardValue);
        } else if (forwardValue instanceof BigDecimal && afterwardValue instanceof BigDecimal) {
            return ((BigDecimal) forwardValue).compareTo((BigDecimal) afterwardValue);
        } else {
            throw new RuntimeException("类型不匹配");
        }
    }

    @SneakyThrows
    public Object getCompareValue(Object target, Object compareObject) {
        if (target instanceof CompareObject) {
            return this.getCustomizeObjectCompareValue(target, compareObject);
        } else if (target instanceof Map) {
            return this.getCacheMethod(target.getClass(), MAP_METHOD_NAME, Object.class).invoke(target, compareObject);
        }
        return this.getCacheField(target.getClass(), compareObject).get(target);
    }

    @SneakyThrows
    protected Object getCustomizeObjectCompareValue(Object target, Object compareObject) {
        return this.getCacheMethod(target.getClass(), CUSTOMIZE_OBJECT_METHOD_NAME, Object.class).invoke(target, compareObject);
    }

}
