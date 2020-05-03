package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class CompletableFutureTests {
    @Test
    public void test2() throws InterruptedException {
        log.info("test2-线程id：{}", Thread.currentThread().getId());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            log.info("runAsync-线程id：{}", Thread.currentThread().getId());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            if (new Random().nextInt() % 2 >= 0) {
                int i = 12 / 0;
            }
            System.out.println("run end ...");
        });

//        future.whenComplete(new BiConsumer<Void, Throwable>() {
//            @Override
//            public void accept(Void t, Throwable action) {
//                log.info("whenComplete-线程id：{}",Thread.currentThread().getId());
//                System.out.println("whenComplete执行完成！");
//            }
//
//        });
        future.whenCompleteAsync(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void t, Throwable action) {
                log.info("whenCompleteAsync-线程id：{}", Thread.currentThread().getId());
                System.out.println("whenCompleteAsync执行完成！");
            }

        }, Executors.newFixedThreadPool(1));
        future.exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable t) {
                log.info("exceptionally-线程id：{}", Thread.currentThread().getId());
                System.out.println("执行失败！" + t.getMessage());
                return null;
            }
        });

        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                long result = new Random().nextInt(100);
                System.out.println("result1=" + result);
                return result;
            }
        }).thenApply(new Function<Long, Long>() {
            @Override
            public Long apply(Long t) {
                long result = t * 5;
                System.out.println("result2=" + result);
                return result;
            }
        });

        long result = future.get();
        System.out.println(result);
    }
}
