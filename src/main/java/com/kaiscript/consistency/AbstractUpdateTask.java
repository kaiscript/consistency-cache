package com.kaiscript.consistency;

/**
 * Created by kaiscript on 2019/5/26.
 */
public abstract class AbstractUpdateTask<T> extends AbstractConsistencyTask{

    @Override
    public int operationType() {
        return OperationType.UPDATE.getValue();
    }

}
