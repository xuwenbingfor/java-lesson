package com.jz.java.concurrent.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class AtomicTests {
    private volatile int field;

    @Test
    public void test6() {
        for (int i = 0; i < 5; i++) {
            demo2(() -> new LongAdder(), adder -> adder.increment());
        }
        System.out.println("-----------------");
        for (int i = 0; i < 5; i++) {
            demo2(() -> new AtomicLong(), adder -> adder.getAndIncrement());
        }
    }

    private <T> void demo2(Supplier<T> adderSupplier, Consumer<T> action) {
        T adder = adderSupplier.get();
        long start = System.nanoTime();
        List<Thread> ts = new ArrayList<>();
        // 40 个线程，每人累加 50 万；合计20000000
        for (int i = 0; i < 40; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(adder + " cost:" + (end - start) / 1000_000);
    }

    @Test
    public void test5() {
        AtomicIntegerFieldUpdater fieldUpdater =
                AtomicIntegerFieldUpdater.newUpdater(AtomicTests.class, "field");
        AtomicTests test5 = new AtomicTests();
        fieldUpdater.compareAndSet(test5, 0, 10);
        // 修改成功 field = 10
        System.out.println(test5.field);
        // 修改成功 field = 20
        fieldUpdater.compareAndSet(test5, 10, 20);
        System.out.println(test5.field);
        // 修改失败 field = 20
        fieldUpdater.compareAndSet(test5, 10, 30);
        System.out.println(test5.field);
    }

    @Test
    public void test4() {
        // 不安全的数组
        // 意图每位元素自增到10000
//        demo(
//                () -> new int[10],
//                (array) -> array.length,
//                (array, index) -> array[index]++,
//                array -> log.info("{}", Arrays.toString(array))
//        );
        // 安全的数组
        demo(
                () -> new AtomicIntegerArray(10),
                (array) -> array.length(),
                (array, index) -> array.getAndIncrement(index),
                array -> System.out.println(array)
        );
    }


    private <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFun,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer) {
        List<Thread> ts = new ArrayList<>();
        T array = arraySupplier.get();
        int length = lengthFun.apply(array);
        for (int i = 0; i < length; i++) {
            // 每个线程对数组作 10000 次操作
            ts.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    putConsumer.accept(array, j % length);
                }
            }));
        }
        // 启动所有线程
        ts.forEach(t -> t.start());
        // 等所有线程结束
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        printConsumer.accept(array);
    }

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
