package com.jz.java.concurrent.pattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuwenbingfor
 * @version 2020/5/4 9:11
 * @description
 */
public class GuardedSuspensionTest1 {
    /**
     * 线程 1 输出 a 5 次，线程 2 输出 b 5 次，线程 3 输出 c 5 次。现在要求输出 abcabcabcabcabc 怎么实现
     * <p>
     * 1、对应设计模式为“保护暂停模式”：条件满足执行，条件不满足等待
     * </p>
     * <p>
     * synchronized解法
     * </p>
     */
    @Test
    public void test1() throws InterruptedException {
        PrintMachine1 printMachine = new PrintMachine1();
        new PrintMan("a", 1, 2, printMachine).start();
        new PrintMan("b", 2, 3, printMachine).start();
        new PrintMan("c", 3, 1, printMachine).start();
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * 条件变量解法
     *
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        PrintMachine2 printMachine = new PrintMachine2();
        new PrintMan("a", 1, 2, printMachine).start();
        new PrintMan("b", 2, 3, printMachine).start();
        new PrintMan("c", 3, 1, printMachine).start();
        TimeUnit.SECONDS.sleep(1);
        printMachine.start();
        TimeUnit.SECONDS.sleep(3);
    }

    static class PrintMan extends Thread {
        private String printContent;
        private int flag;
        private int nextFlag;
        private AbstractPrintMachine printMachine;

        PrintMan(String printContent, int flag, int nextFlag, AbstractPrintMachine printMachine) {
            this.printContent = printContent;
            this.flag = flag;
            this.nextFlag = nextFlag;
            this.printMachine = printMachine;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                printMachine.print(printContent, flag, nextFlag);
            }
        }
    }

    @Slf4j
    static abstract class AbstractPrintMachine {
        protected int waitFlag = 1;

        public abstract void print(String printContent, int flag, int nextFlag);
    }

    @Slf4j
    static class PrintMachine2 extends AbstractPrintMachine {
        private Lock lock = new ReentrantLock();
        private List<Condition> conditionList = new ArrayList<>(3);

        PrintMachine2() {
            conditionList.add(lock.newCondition());
            conditionList.add(lock.newCondition());
            conditionList.add(lock.newCondition());
        }

        @Override
        public void print(String printContent, int flag, int nextFlag) {
            lock.lock();
            try {
                await(flag);
                log.info("线程{}打印{}", Thread.currentThread().getId(), printContent);
                this.waitFlag = nextFlag;
                signal(this.waitFlag);
            } finally {
                lock.unlock();
            }
        }

        public void start() {
            lock.lock();
            try {
                signal(this.waitFlag);
            } finally {
                lock.unlock();
            }
        }

        private void await(int flag) {
            try {
                conditionList.get(flag - 1).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void signal(int flag) {
            conditionList.get(flag - 1).signal();
        }
    }

    @Slf4j
    static class PrintMachine1 extends AbstractPrintMachine {

        @Override
        public void print(String printContent, int flag, int nextFlag) {
            synchronized (this) {
                while (waitFlag != flag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.info("线程{}打印{}", Thread.currentThread().getId(), printContent);
                this.waitFlag = nextFlag;
                this.notifyAll();
            }
        }
    }
}
