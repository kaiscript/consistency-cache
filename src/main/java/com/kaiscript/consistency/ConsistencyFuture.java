package com.kaiscript.consistency;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.concurrent.TimeUnit;

/**
 * Created by kaiscript on 2019/5/25.
 */
public class ConsistencyFuture<T>{

    private volatile T result;

    public void setResult(T t) {
        result = t;
    }

    /**
     * 阻塞获取结果
     * @return
     */
    public T get() {
        for (;;){
            if (result != null) {
                return result;
            }
        }
    }

    /**
     * 时间间隔内获取结果
     * @param value
     * @param unit
     * @return
     * @throws Exception
     */
    public T get(int value, TimeUnit unit) throws Exception{
        long now = System.currentTimeMillis();
        for (;;){
            if (result != null) {
                return result;
            }
            if (System.currentTimeMillis() - now > TimeUnit.MILLISECONDS.convert(value, unit)) {
                throw new RuntimeException("get result timeout");
            }
        }
    }


    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
