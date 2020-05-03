package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CyclicBarrierTests {
    @Test
    public void test1() throws InterruptedException {
        final Integer threadCount = 5;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount, () -> {
            log.info("所有运动员到达栅栏");
        });
        for (int index = 0; index < threadCount; index++) {
            new Thread(() -> {
                try {
                    // 栅栏1
                    TimeUnit.SECONDS.sleep((int) (Math.random() * 10));
                    log.info("运动员{}到达栅栏1", Thread.currentThread().getId());
                    cyclicBarrier.await();
                    log.info("运动员{}冲破栅栏1", Thread.currentThread().getId());

                    // 栅栏2
                    TimeUnit.SECONDS.sleep((int) (Math.random() * 10));
                    log.info("运动员{}到达栅栏2", Thread.currentThread().getId());
                    cyclicBarrier.await();
                    log.info("运动员{}冲破栅栏2", Thread.currentThread().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        TimeUnit.MINUTES.sleep(3);
    }

    @Test
    public void test2() throws InterruptedException {
        log.info("{}",(int) (Math.random() * 100));
    }
}
