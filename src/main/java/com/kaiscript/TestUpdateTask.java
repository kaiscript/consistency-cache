package com.kaiscript;

import com.kaiscript.consistency.AbstractUpdateTask;

/**
 * Created by kaiscript on 2019/5/26.
 */
public class TestUpdateTask extends AbstractUpdateTask<Integer> {

    @Override
    public String key() {
        return "test";
    }

    @Override
    public Integer loadData() {
        return 1234;
    }

}
