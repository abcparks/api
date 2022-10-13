package cn.alex.batch;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by WCY on 2022/9/28
 */
public class BatchHandler {

    // 自定义
    private Integer limit;

    public BatchHandler limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    // 单例
    private static volatile BatchHandler batchHandler;

    public static BatchHandler getInstance() {
        if (batchHandler == null) {
            synchronized (BatchHandler.class) {
                if (batchHandler == null) {
                    batchHandler = new BatchHandler().limit(1000);
                }
            }
        }
        return batchHandler;
    }

    public void batchInsert(Collection<?> paramList, Consumer<Collection<?>> consumer) {
        if (CollectionUtils.isNotEmpty(paramList)) {
            if (paramList.size() <= limit) {
                consumer.accept(paramList);
                return;
            }
            List<Object> insertList = new ArrayList<>();
            for (Object param : paramList) {
                insertList.add(param);
                if (insertList.size() == limit) {
                    consumer.accept(insertList);
                    insertList.clear();
                }
            }
            if (insertList.size() > 0) {
                consumer.accept(insertList);
            }
        }
    }

    public void batchDelete(Collection<?> paramList, Consumer<Collection<?>> consumer) {
        if (CollectionUtils.isNotEmpty(paramList)) {
            if (paramList.size() <= limit) {
                consumer.accept(paramList);
                return;
            }
            List<Object> deleteList = new ArrayList<>();
            for (Object param : paramList) {
                deleteList.add(param);
                if (deleteList.size() == limit) {
                    consumer.accept(deleteList);
                    deleteList.clear();
                }
            }
            if (deleteList.size() > 0) {
                consumer.accept(deleteList);
            }
        }
    }

    public void batchUpdate(Collection<?> paramList, Consumer<Collection<?>> consumer) {
        if (CollectionUtils.isNotEmpty(paramList)) {
            if (paramList.size() <= limit) {
                consumer.accept(paramList);
                return;
            }
            List<Object> updateList = new ArrayList<>();
            for (Object param : paramList) {
                updateList.add(param);
                if (updateList.size() == limit) {
                    consumer.accept(updateList);
                    updateList.clear();
                }
            }
            if (updateList.size() > 0) {
                consumer.accept(updateList);
            }
        }
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
        BatchHandler batchHandler = BatchHandler.getInstance();
        batchHandler = new BatchHandler().limit(3);
        List<Integer> dataList = Arrays.asList(1, 2, 3, 4, 5);
        batchHandler.batchInsert(dataList, System.out::println);
        List<String> resultList = batchHandler.batchQuery(dataList, list -> list.stream().map(String::valueOf).collect(Collectors.toList()));
        System.out.println(resultList);
    }

}
