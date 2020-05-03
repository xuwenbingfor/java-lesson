package com.jz.java.concurrent.api;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class ReadWriteLockTests {
    @Test
    public void test2() throws InterruptedException {
        // 读-读能共存
        // 读-写不能共存
        // 写-写不能共存
        Cache cache = new Cache();
        new Thread(() -> {
            cache.get("key");
        }).start();
        TimeUnit.SECONDS.sleep(1);
        log.info("1s后启动写线程");
        new Thread(() -> {
            cache.put("key", "value");
        }).start();
        TimeUnit.MINUTES.sleep(5);
        log.info("主线程结束");
    }

    @Test
    public void test1() throws InterruptedException {
        Cache cache = new Cache();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                while (true) {
                    cache.get("key");
                }
            }).start();
        }

        TimeUnit.SECONDS.sleep(30);
        log.info("30s后新线程开始写，持续1m");
        new Thread(() -> {
            cache.put("key", "value");
        }).start();

        TimeUnit.MINUTES.sleep(5);
        log.info("主线程结束");
    }

    @Slf4j
    static class Cache {
        private Map<String, String> cache = new HashMap<>();
        private ReadWriteLock lock = new ReentrantReadWriteLock();
        private Lock readLock = lock.readLock();
        private Lock writeLock = lock.writeLock();

        public String get(String key) {
            readLock.lock();
            try {
                log.info("线程:{}读", Thread.currentThread().getId());
                TimeUnit.SECONDS.sleep(10);
                return cache.get(key);
            } catch (Exception e) {
                log.error("error", e);
            } finally {
                readLock.unlock();
            }
            return null;
        }

        public void put(String key, String value) {
            writeLock.lock();
            try {
                log.info("线程:{}写", Thread.currentThread().getId());
                TimeUnit.MINUTES.sleep(1);
                cache.put(key, value);
            } catch (Exception e) {
                log.error("error", e);
            } finally {
                writeLock.unlock();
            }
        }
    }
}
