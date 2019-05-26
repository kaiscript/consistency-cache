//package com.kaiscript.jmh;
//
//import com.kaiscript.TestTask;
//import com.kaiscript.consistency.ConsistencyDispatcher;
//import com.kaiscript.consistency.OperationType;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by kaiscript on 2019/5/26.
// */
//@BenchmarkMode(Mode.Throughput)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
//public class TaskBenchmark {
//
//    ConsistencyDispatcher dispatcher = new ConsistencyDispatcher(1, 0);
//
//    TestTask queryTask = new TestTask("uid", OperationType.QUERY.getValue());
//
//    TestTask updateTask = new TestTask("uid", OperationType.UPDATE.getValue());
//
//    public static void main(String[] args) throws Exception{
//        Options opt = new OptionsBuilder()
//                .output("./out.log")
//                .include(TaskBenchmark.class.getSimpleName())
//                .forks(1)
//                .warmupIterations(5)
//                .measurementIterations(5)
//                .build();
//
//        new Runner(opt).run();
//    }
//
//    @Benchmark
//    public void dispatcher() {
//        dispatcher.dispatcher(queryTask);
//        dispatcher.dispatcher(updateTask);
//    }
//
//}
