package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SemaphoreTests {
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
     * 测试信号量机制解决生产者/消费者问题
     */
    @Slf4j
    static class BreadBox {
        private Integer breadCount = 0;
        private Semaphore mutex = new Semaphore(1);
        private Semaphore notEmpty = new Semaphore(0);
        private Semaphore notFull = new Semaphore(10);

        public void product() throws InterruptedException {
            notFull.acquire();
            mutex.acquire();
            try {
                breadCount++;
                log.info("生产者{}生产一个面包，当前面包数：{}", Thread.currentThread().getId(), breadCount);
            } finally {
                mutex.release();
                notEmpty.release();
            }
        }

        public void consume() throws InterruptedException {
            notEmpty.acquire();
            mutex.acquire();
            try {
                breadCount--;
                log.info("消费者{}消费一个面包，当前面包数：{}", Thread.currentThread().getId(), breadCount);
            } finally {
                mutex.release();
                notFull.release();
            }
        }
    }
}