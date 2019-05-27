package com.kaiscript.consistency;

import com.kaiscript.consistency.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kaiscript on 2019/5/25.
 */
public class ConsistencyDispatcher<T extends AbstractConsistencyTask> {

    private static final Logger logger = LoggerFactory.getLogger(ConsistencyDispatcher.class);

    private HashMap<String, Object> cache = new HashMap<>();

    private int queueNum;

    private int consumePeriod = 0;

    private Map<Integer, ConcurrentLinkedQueue<T>> queueMap = new HashMap<>();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public ConsistencyDispatcher(int queueNum, int consumePeriod) {
        this.queueNum = queueNum;
        this.consumePeriod = consumePeriod;
        for (int i = 0; i < queueNum; i++) {
            queueMap.put(i, new ConcurrentLinkedQueue<>());
        }
        init();
    }

    private void init() {
        queueMap.forEach((index, queue) -> {
            executorService.submit(() -> {
                while (true) {
                    T task = queue.poll();
                    if (task == null) {
                        continue;
                    }
                    logger.info("{}-queue poll task:{}", index, task);
                    exec(task, queue);
                    if (consumePeriod != 0) {
                        CommonUtil.sleep(consumePeriod);
                    }
                }
            });
        });
    }

    public ConsistencyFuture dispatcher(T task) {
        ConsistencyContext context = task.getContext();
        int index = context.getKey().hashCode() % queueNum;
        if (!checkCache(task)) {
            queueMap.get(index).offer(task);
        }
        logger.info("dispatcher task:{}", task);
        return task.getFuture();
    }

    private boolean checkCache(T task) {
        ConsistencyContext context = task.getContext();
        if (context.getOperationType() == OperationType.QUERY.getValue()) {
            Object o = cache.get(context.getKey());
            if (o != null) {
                task.notifyResult(o);
                return true;
            }
        }
        return false;
    }


    private void exec(T task, Queue<T> queue) {
        ConsistencyContext context = task.getContext();
        int operationType = context.getOperationType();
        switch (operationType){
            case 1:
                update(task, queue);
                break;
            case 2:
                queryOrUpdate(task, queue);
                break;
            case 3:
                removeCache(task, queue);
                break;
            default:
                break;
        }

    }

    private void queryOrUpdate(T task, Queue<T> queue) {
        ConsistencyContext context = task.getContext();
        String key = context.getKey();
        Object data = cache.get(key);
        if (data != null) {
            task.notifyResult(data);
        }
        else {
            try {
                T updateTask = buildUpdateTask(task);
                queue.offer(updateTask);
                logger.info("queryOrUpdate.offer update task:{}", updateTask);
                if (task instanceof AbstractQueryTask) {
                    AbstractQueryTask queryTask = (AbstractQueryTask) task;
                    if (queryTask.needQueryRetry()) {
                        logger.info("needQueryRetry queryOrUpdate.offer update task:{}", task);
                        queue.offer(task);
                    }
                }

            } catch (Exception e) {
                logger.error("queryOrUpdate task:{},error", task, e);
            }
        }
    }

    private void update(T task, Queue<T> queue) {
        ConsistencyContext context = task.getContext();
        String key = context.getKey();
        cache.remove(key);
        Object data = task.loadData();
        logger.info("UPDATE task:{},data:{}", task, data);
        if (data != null) {
            cache.put(key, data);
            task.notifyResult(data);
        }
    }


    private void removeCache(T task, Queue<T> queue) {
        cache.remove(task.getContext().getKey());
    }

    private T buildUpdateTask(T task) {
        try {
            T updateTask = (T) task.getClass().newInstance();
            updateTask.setContext(task.getContext());
            updateTask.setFuture(task.getFuture());
            updateTask.getContext().setOperationType(OperationType.UPDATE.getValue());
            return updateTask;
        } catch (Exception e) {
            throw new RuntimeException("buildUpdateTask e", e);
        }

    }

}
