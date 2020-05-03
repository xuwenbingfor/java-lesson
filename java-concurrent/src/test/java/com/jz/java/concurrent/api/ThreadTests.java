package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadTests {
    @Test
    public void test6() throws InterruptedException {
        // 线程终止，join将不再等待
        Thread thread1 = new Thread(() -> {
            log.info("thread1 over");
        });
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        thread1.join();
        log.info("main thread run");
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void test5() throws InterruptedException {
        // 4、完全不影响NEW、TERMINATED状态的线程
        Thread thread1 = new Thread(() -> {
            log.info("thread1 over");
        });
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("thread status:{}", thread1.getState());
        log.info("thread isInterrupted:{}", thread1.isInterrupted());
        thread1.interrupt();
        log.info("thread isInterrupted:{}", thread1.isInterrupted());
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void test4() throws InterruptedException {
        // 3、可以中断WAITING、TIME_WAITING，此时抛出InterruptedException异常
        Thread thread1 = new Thread(() -> {
            log.info("thread1 come in");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.info("thread1 isInterrupted {}", Thread.currentThread().isInterrupted());
            }
            log.info("thread1 gone away");
        });
        thread1.start();
        TimeUnit.SECONDS.sleep(5);
        log.info("after 5s thread1 interrupt");
        log.info("thread status:{}", thread1.getState());
        thread1.interrupt();
        TimeUnit.SECONDS.sleep(20);
    }

    @Test
    public void test3() throws InterruptedException {
        // 2、无法中断BLOCKED状态的线程
        String share = "share";
        Thread thread1 = new Thread(() -> {
            log.info("thread1 come in");
            synchronized (share) {
                try {
                    log.info("thread1 is doing");
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("thread1 gone away");
        });
        thread1.start();
        TimeUnit.SECONDS.sleep(1);
        Thread thread2 = new Thread(() -> {
            log.info("thread2 come in");
            synchronized (share) {
                log.info("thread2 is doing");
                log.info("thread2 isInterrupted {}", Thread.currentThread().isInterrupted());
            }
            log.info("thread2 gone away");
        });
        thread2.start();
        TimeUnit.SECONDS.sleep(1);
        log.info("thread2 interrupt");
        log.info("thread2 status:{}", thread2.getState());
        thread2.interrupt();
        TimeUnit.SECONDS.sleep(20);
    }

    @Test
    public void test2() throws InterruptedException {
        // 1、无法中断RUNNABLE状态的线程
        Thread thread = new Thread(() -> {
            while (true) {
                log.info("{} is running", Thread.currentThread().getName());
            }
        }, "测试线程");
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        thread.interrupt();
        log.info("after 10 s interrupt {}", thread.getName());
        log.info("{} isInterrupted {}", thread.getName(), thread.isInterrupted());
        TimeUnit.SECONDS.sleep(1);
    }


    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            log.info("thread priority is {}", Thread.currentThread().getPriority());
        }, "测试线程");
        // 设置为守护线程
        thread.start();
        log.info("main thread priority is {}", Thread.currentThread().getPriority());
    }


/*    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("thread name is {}", Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("thread is over");
        }, "测试线程");
        // 设置为守护线程
//        thread.setDaemon(true);
        thread.start();
        log.info("{}", Thread.currentThread().isDaemon());
        log.info("main thread is start");
        TimeUnit.SECONDS.sleep(30);
        log.info("main thread is over");
    }*/

    @Test
    public void test1() {
        new Thread(() -> {
            log.info("thread name is {}", Thread.currentThread().getName());
        }, "测试线程").start();
    }
}
