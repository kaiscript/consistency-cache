package com.kaiscript.consistency;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by kaiscript on 2019/5/25.
 */
public abstract class AbstractConsistencyTask<T>{

    private ConsistencyFuture future = new ConsistencyFuture();

    private ConsistencyContext context = new ConsistencyContext();

    public abstract T loadData();

    public abstract String getKey();

    public abstract int operationType();

    public AbstractConsistencyTask() {
        context.setKey(getKey());
        context.setOperationType(operationType());
    }

    public void notifyResult(T t) {
        future.setResult(t);
    }

    public ConsistencyFuture getFuture() {
        return future;
    }

    public void setFuture(ConsistencyFuture future) {
        this.future = future;
    }

    public ConsistencyContext getContext() {
        return context;
    }

    public void setContext(ConsistencyContext context) {
        this.context = context;
    }




    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
