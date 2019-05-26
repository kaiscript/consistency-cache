package com.kaiscript;

import com.kaiscript.consistency.AbstractQueryTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by kaiscript on 2019/5/25.
 */
public class TestTask extends AbstractQueryTask<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(TestTask.class);

    @Override
    public String getKey() {
        return "test";
    }

    @Override
    public Integer loadData() {
        Random random = new Random();
        int num = random.nextInt();
        logger.info("load data.key:{},type:{},result:{}", getContext().getKey(), getContext().getOperationType(),1234);
//        return num % 2 == 1 ? 1234 : null;
        return null;
    }

}
