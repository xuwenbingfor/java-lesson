package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author xuwenbingfor
 * @version 2020/5/11 22:24
 * @description
 */
@Slf4j
public class ThreadPoolTests {
    @Test
    public void test1() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Callable<Integer> callable2 = new Callable<>() {
            @Override
            public Integer call() throws Exception {
                TimeUnit.SECONDS.sleep(2);
                return 2;
            }
        };
        Callable<Integer> callable4 = new Callable<>() {
            @Override
            public Integer call() throws Exception {
                TimeUnit.SECONDS.sleep(4);
                return 4;
            }
        };
        Callable<Integer> callable6 = new Callable<>() {
            @Override
            public Integer call() throws Exception {
                TimeUnit.SECONDS.sleep(6);
                return 6;
            }
        };
        long time1 = System.currentTimeMillis();
        log.info("time1：{}", time1);
        List<Future<Integer>> futures = executorService.invokeAll(Arrays.asList(callable2, callable4, callable6),
                5, TimeUnit.SECONDS);
        for (Future<Integer> future : futures) {
            log.info("{}", future.get());
        }
        long time2 = System.currentTimeMillis();
        log.info("time2：{}", time2);
        log.info("执行总耗时：{}s", (time2 - time1) / 1000);
    }
}
