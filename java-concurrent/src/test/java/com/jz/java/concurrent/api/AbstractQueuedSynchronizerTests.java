package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author xuwenbingfor
 * @version 2020/5/17 21:36
 * @description
 */
@Slf4j
public class AbstractQueuedSynchronizerTests {
    private int count = 0;

    @Test
    public void test1() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(3000);
        for (int index = 0; index < 3000; index++) {
            new Thread(() -> {
                count++;
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        log.info("3000个线程执行完毕，count：{}", count);
    }

    @Test
    public void test2() throws InterruptedException {
        Mutex mutex = new Mutex();
        CountDownLatch countDownLatch = new CountDownLatch(3000);
        for (int index = 0; index < 3000; index++) {
            new Thread(() -> {
                mutex.lock();
                try {
                    count++;
                } finally {
                    countDownLatch.countDown();
                    mutex.unlock();
                }
            }).start();
        }
        countDownLatch.await();
        log.info("3000个线程执行完毕，count：{}", count);
    }

    static class Mutex implements Lock, java.io.Serializable {
        // 自定义同步器
        private static class Sync extends AbstractQueuedSynchronizer {
            // 判断是否锁定状态
            protected boolean isHeldExclusively() {
                return getState() == 1;
            }

            // 尝试获取资源，立即返回。成功则返回true，否则false。
            public boolean tryAcquire(int acquires) {
                assert acquires == 1; // 这里限定只能为1个量
                if (compareAndSetState(0, 1)) {//state为0才设置为1，不可重入！
                    setExclusiveOwnerThread(Thread.currentThread());//设置为当前线程独占资源
                    return true;
                }
                return false;
            }

            // 尝试释放资源，立即返回。成功则为true，否则false。
            protected boolean tryRelease(int releases) {
                assert releases == 1; // 限定为1个量
                if (getState() == 0)//既然来释放，那肯定就是已占有状态了。只是为了保险，多层判断！
                    throw new IllegalMonitorStateException();
                setExclusiveOwnerThread(null);
                setState(0);//释放资源，放弃占有状态
                return true;
            }
        }

        // 真正同步类的实现都依赖继承于AQS的自定义同步器！
        private final Sync sync = new Sync();

        //lock<-->acquire。两者语义一样：获取资源，即便等待，直到成功才返回。
        public void lock() {
            sync.acquire(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        //tryLock<-->tryAcquire。两者语义一样：尝试获取资源，要求立即返回。成功则为true，失败则为false。
        public boolean tryLock() {
            return sync.tryAcquire(1);
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        //unlock<-->release。两者语文一样：释放资源。
        public void unlock() {
            sync.release(1);
        }

        @Override
        public Condition newCondition() {
            return null;
        }

        //锁是否占有状态
        public boolean isLocked() {
            return sync.isHeldExclusively();
        }
    }
}
