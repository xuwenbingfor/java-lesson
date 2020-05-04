package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ReentrantLockTests {
    @Test
    public void test2() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            log.debug("启动...");
            // 不可中断模式
            lock.lock();
            log.info("中断状态：{}",Thread.currentThread().isInterrupted());
/*            try {
//                 可中断模式
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("等锁的过程中被打断");
                log.info("中断状态：{}",Thread.currentThread().isInterrupted());
                return;
            }*/
            try {
                log.debug("获得了锁");
            } finally {
                log.debug("释放了锁");
                lock.unlock();
            }
        }, "t1");
        lock.lock();
        log.debug("获得了锁");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
            t1.interrupt();
            log.debug("执行打断");
            TimeUnit.SECONDS.sleep(1);
        } finally {
            log.debug("释放了锁");
            lock.unlock();
        }
    }

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
