package com.kaiscript.consistency;

/**
 * Created by kaiscript on 2019/5/25.
 */
public enum OperationType {

    UPDATE(1),
    QUERY(2),
    DELETE(3),
    UPDATE_CACHE(4);

    private int value;

    OperationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
