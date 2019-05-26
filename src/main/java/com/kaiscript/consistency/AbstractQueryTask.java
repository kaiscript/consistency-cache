package com.kaiscript.consistency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kaiscript on 2019/5/26.
 */
public abstract class AbstractQueryTask<T> extends AbstractConsistencyTask<T> {

    /**
     * 查询多少次就不查询了
     */
    private int queryRetryTime = 1;

    private AtomicInteger queryRetryCount = new AtomicInteger(0);

    public void setQueryRetryTime(int queryRetryTime) {
        this.queryRetryTime = queryRetryTime;
    }

    public boolean needQueryRetry() {
        if (queryRetryTime < 0) {
            return false;
        }
        int ret = queryRetryCount.incrementAndGet();
        if (ret > queryRetryTime) {
            queryRetryCount.set(1);
            return false;
        }
        return true;
    }

    @Override
    public int operationType() {
        return OperationType.QUERY.getValue();
    }
}
