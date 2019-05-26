package com.kaiscript;

import com.kaiscript.consistency.ConsistencyDispatcher;
import com.kaiscript.consistency.ConsistencyFuture;
import com.kaiscript.consistency.util.CommonUtil;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws Exception{

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        ConsistencyDispatcher dispatcher = new ConsistencyDispatcher(1, 500);
        TestTask queryTask = new TestTask();
        queryTask.setQueryRetryTime(3);
        TestUpdateTask updateTask = new TestUpdateTask();
        executorService.submit(() -> {
            try {
                cyclicBarrier.await();
                ConsistencyFuture ret = dispatcher.dispatcher(queryTask);
                System.out.println("ret1: " + ret.get());
            }  catch (Exception e) {
                e.printStackTrace();
            }

        });
        executorService.submit(() -> {
            try {
                cyclicBarrier.await();
                CommonUtil.sleep(500);
//                ConsistencyFuture result = dispatcher.dispatcher(queryTask);
//                System.out.println("ret2: " + result.get());
            }  catch (Exception e) {
                e.printStackTrace();
            }

        });

    }
}
