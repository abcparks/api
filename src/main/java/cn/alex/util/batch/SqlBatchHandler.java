package cn.alex.util.batch;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by WCY on 2025/11/13
 */
public class SqlBatchHandler {

    // 自定义
    private Integer limit;

    public SqlBatchHandler limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    // 单例
    private static volatile SqlBatchHandler batchHandler;

    public static SqlBatchHandler getInstance() {
        if (batchHandler == null) {
            synchronized (SqlBatchHandler.class) {
                if (batchHandler == null) {
                    batchHandler = new SqlBatchHandler().limit(1000);
                }
            }
        }
        return batchHandler;
    }

    public void batch(Collection<?> paramList, Consumer<Collection<?>> consumer) {
        if (CollectionUtils.isNotEmpty(paramList)) {
            if (paramList.size() <= limit) {
                consumer.accept(paramList);
                return;
            }
            List<Object> list = new ArrayList<>();
            for (Object param : paramList) {
                list.add(param);
                if (list.size() == limit) {
                    consumer.accept(list);
                    list.clear();
                }
            }
            if (list.size() > 0) {
                consumer.accept(list);
            }
        }
    }

    public void batchInsert(Collection<?> insertList, Consumer<Collection<?>> consumer) {
        this.batch(insertList, consumer);
    }

    public void batchDelete(Collection<?> deleteList, Consumer<Collection<?>> consumer) {
        this.batch(deleteList, consumer);
    }

    public void batchUpdate(Collection<?> updadteList, Consumer<Collection<?>> consumer) {
        this.batch(updadteList, consumer);
    }

    // 注意: 批量查询需保证查询参数唯一性
    public <T> List<T> batchQuery(Collection<?> paramList, Function<Collection<?>, List<T>> function) {
        if (CollectionUtils.isNotEmpty(paramList)) {
            if (paramList.size() <= limit) {
                return function.apply(paramList);
            }
            List<T> resultList = new ArrayList<>();
            List<Object> queryList = new ArrayList<>();
            for (Object param : paramList) {
                queryList.add(param);
                if (queryList.size() == limit) {
                    resultList.addAll(function.apply(queryList));
                    queryList.clear();
                }
            }
            if (queryList.size() > 0) {
                resultList.addAll(function.apply(queryList));
            }
            return resultList;
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        SqlBatchHandler batchHandler = SqlBatchHandler.getInstance();
        batchHandler = new SqlBatchHandler().limit(3);
        List<Integer> dataList = Arrays.asList(1, 2, 3, 4, 5);
        batchHandler.batchInsert(dataList, System.out::println);
        List<String> resultList = batchHandler.batchQuery(dataList, list -> list.stream().map(String::valueOf).collect(Collectors.toList()));
        System.out.println(resultList);
    }

}
