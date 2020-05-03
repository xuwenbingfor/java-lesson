package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ReentrantLockTests {
    @Test
    public void test1() throws InterruptedException {
        Counter counter = new Counter();
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
        private Integer count = 0;
        private Lock lock = new ReentrantLock();

        // 存在并发问题
//    public void addOne() {
//        log.info("线程{} addOne 开始", Thread.currentThread().getId());
//        this.count++;
//        log.info("线程{} addOne 结束", Thread.currentThread().getId());
//    }

        public void addOne() {
            lock.lock();
            try {
                log.info("线程{} addOne 开始", Thread.currentThread().getId());
                this.count++;
                log.info("线程{} addOne 结束", Thread.currentThread().getId());
            } finally {
                lock.unlock();
            }
        }

        public void printCount() {
            log.info("count:{}", this.count);
        }
    }
}
