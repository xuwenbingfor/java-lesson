package com.jz.java.concurrent.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

@Slf4j
public class AtomicTests {
    @Test
    public void test3() throws InterruptedException {
        User user1 = new User("yellow", 1);
        User user2 = new User("red", 2);
        AtomicMarkableReference<User> userAtomicStampedReference =
                new AtomicMarkableReference<User>(user1, false);

//        boolean marked = userAtomicStampedReference.isMarked();
//        log.info("marked:{}",marked);
//        log.info("marked:{}",marked);

        // 发生了ABA问题
        new Thread(() -> {
            boolean b = userAtomicStampedReference.compareAndSet(user1, user2,
                    false, true);
            log.info("compareAndSet result:{}", b);
            log.info("user:{}", userAtomicStampedReference.getReference());
        }, "thread1").start();

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            boolean b = userAtomicStampedReference.compareAndSet(user2, user1,
                    true, false);
            log.info("compareAndSet result:{}", b);
            log.info("user:{}", userAtomicStampedReference.getReference());
        }, "thread2").start();

        TimeUnit.SECONDS.sleep(1);
//        boolean b = userAtomicStampedReference.compareAndSet(user1, user2,
//                false, true);
        boolean b = userAtomicStampedReference.compareAndSet(user1, user2,
                true, false);
        log.info("compareAndSet result:{}", b);
        log.info("user:{}", userAtomicStampedReference.getReference());
    }

    @Test
    public void test2() throws InterruptedException {
        User user1 = new User("yellow", 1);
        User user2 = new User("red", 2);
        User user3 = new User("green", 3);
        AtomicStampedReference<User> userAtomicStampedReference =
                new AtomicStampedReference<User>(user1, 0);
//        log.info("stamp:{}", userAtomicStampedReference.getStamp());
//        log.info("stamp:{}", userAtomicStampedReference.getStamp());
//        log.info("stamp:{}", userAtomicStampedReference.getStamp());

//        userAtomicStampedReference.compareAndSet(user1, user2,
//                userAtomicStampedReference.getStamp(),
//                userAtomicStampedReference.getStamp() + 1);
//        log.info("user:{}", userAtomicStampedReference.getReference());

        int stamp = userAtomicStampedReference.getStamp();
        // 发生了ABA问题
        new Thread(() -> {
            boolean b = userAtomicStampedReference.compareAndSet(user1, user2,
                    userAtomicStampedReference.getStamp(),
                    userAtomicStampedReference.getStamp() + 1);
            log.info("compareAndSet result:{}", b);
            log.info("user:{}", userAtomicStampedReference.getReference());
        }, "thread1").start();

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            boolean b = userAtomicStampedReference.compareAndSet(user2, user1,
                    userAtomicStampedReference.getStamp(),
                    userAtomicStampedReference.getStamp() + 1);
            log.info("compareAndSet result:{}", b);
            log.info("user:{}", userAtomicStampedReference.getReference());
        }, "thread2").start();

        TimeUnit.SECONDS.sleep(1);
        boolean b = userAtomicStampedReference.compareAndSet(user1, user2,
                stamp, stamp + 1);
        log.info("compareAndSet result:{}", b);
        log.info("user:{}", userAtomicStampedReference.getReference());
    }


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

    @Getter
    @ToString
    @AllArgsConstructor
    static class User {
        String userName;

        int age;
    }
}
