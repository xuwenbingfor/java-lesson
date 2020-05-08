package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author xuwenbingfor
 * @version 2020/5/8 22:24
 * @description
 */
@Slf4j
public class LockSupportTests {
    @Test
    public void test1() throws InterruptedException {
        // 先 park 再 unpark
        Thread thread = new Thread(() -> {
            log.info("I am hungry！");
            log.info("waiting food");
            LockSupport.park();
            log.info("I am ok");
        });
        thread.start();
        TimeUnit.SECONDS.sleep(3);
        log.info("god give you food");
        LockSupport.unpark(thread);
    }

    @Test
    public void test2() throws InterruptedException {
        // 先 unpark 再 park
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("I am hungry！");
            log.info("waiting food");
            LockSupport.park();
            log.info("I am ok 1");
            LockSupport.park();
            log.info("I am ok 2");
        });
        thread.start();
        log.info("god give you food");
        LockSupport.unpark(thread);
        TimeUnit.SECONDS.sleep(3);
    }
}
