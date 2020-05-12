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
    public void test3() throws InterruptedException, ExecutionException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        // 每隔1秒执行一次任务
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            log.info("线程{} running", Thread.currentThread().getId());
//        }, 0, 1, TimeUnit.SECONDS);
//        TimeUnit.SECONDS.sleep(10);

        // 每隔任务之间隔1秒
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            log.info("线程{} running", Thread.currentThread().getId());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("线程{} over", Thread.currentThread().getId());
        }, 0, 1, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void test2() throws InterruptedException, ExecutionException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        // 延迟多少秒后执行
        scheduledExecutorService.schedule(() -> {
            log.info("线程{} running", Thread.currentThread().getId());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 1 / 0;
        }, 1, TimeUnit.SECONDS);
        scheduledExecutorService.schedule(() -> {
            log.info("线程{} running", Thread.currentThread().getId());
        }, 1, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void test1() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Callable<Integer> callable2 = () -> {
            TimeUnit.SECONDS.sleep(2);
            return 2;
        };
        Callable<Integer> callable4 = () -> {
            TimeUnit.SECONDS.sleep(4);
            return 4;
        };
        Callable<Integer> callable6 = () -> {
            TimeUnit.SECONDS.sleep(6);
            return 6;
        };
        long time1 = System.currentTimeMillis();
        log.info("time1：{}", time1);
        List<Future<Integer>> futures = executorService.invokeAll(Arrays.asList(callable2, callable4, callable6),
                5, TimeUnit.SECONDS);
        for (Future<Integer> future : futures) {
            try {
                log.info("{}", future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long time2 = System.currentTimeMillis();
        log.info("time2：{}", time2);
        log.info("执行总耗时：{}s", (time2 - time1) / 1000);
    }
}
