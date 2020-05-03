package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ConditionTests {
    @Test
    public void test1() throws InterruptedException {
        BreadBox breadBox = new BreadBox();
        // 启动10个生产者线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        breadBox.product();
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }
        // 启动10个消费者线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        breadBox.consume();
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        TimeUnit.MINUTES.sleep(2);
    }

    /**
     * 面包盒子
     * 测试管程机制解决生产者/消费者问题
     */
    @Slf4j
    static class BreadBox {
        private static final Integer MAX_BREAD_COUNT = 10;
        private Integer breadCount = 0;
        private Lock lock = new ReentrantLock();
        private Condition empty = lock.newCondition();
        private Condition full = lock.newCondition();

        public void product() throws InterruptedException {
            lock.lock();
            try {
                while (breadCount.equals(MAX_BREAD_COUNT)) {
                    full.await();
                }
                breadCount++;
                log.info("生产者{}生产一个面包，当前面包数：{}", Thread.currentThread().getId(), breadCount);
                empty.signal();
            } finally {
                lock.unlock();
            }
        }

        public void consume() throws InterruptedException {
            lock.lock();
            try {
                while (breadCount.equals(0)) {
                    empty.await();
                }
                breadCount--;
                log.info("消费者{}消费一个面包，当前面包数：{}", Thread.currentThread().getId(), breadCount);
                full.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}