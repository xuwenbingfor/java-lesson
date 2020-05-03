package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicTests {
    @Test
    public void test1() throws InterruptedException {
        ReentrantLockTests.Counter counter = new ReentrantLockTests.Counter();
        for (int i = 0; i < 10000; i++) {
            new Thread(() -> {
                counter.addOne();
            }).start();
        }
        TimeUnit.SECONDS.sleep(50);
        counter.printCount();
    }

    /**
     * 计数器
     */
    @Slf4j
    static class Counter {
        private AtomicInteger atomicInteger = new AtomicInteger(0);

        public void addOne() {
            atomicInteger.getAndIncrement();
        }

        public void printCount() {
            log.info("count:{}", atomicInteger.get());
        }
    }
}
