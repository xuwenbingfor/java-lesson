package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author xuwenbingfor
 * @version 2020/5/13 20:41
 * @description
 */
@Slf4j
public class ForkJoinPoolTests {
    @Test
    public void test6() {
        MyTask myTask = new MyTask(0, 100);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            ForkJoinTask<Integer> submit = forkJoinPool.submit(myTask);
            log.info("计算结果：{}", submit.get());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            forkJoinPool.shutdown();
        }
    }

    class MyTask extends RecursiveTask<Integer> {
        private int start;
        private int end;
        private int result;

        public MyTask(int start, int end) {
            this.start = start;
            this.end = end;
            this.result = 0;
        }

        @Override
        protected Integer compute() {
            if ((end - start) < 10) {
                log.info("线程{}计算({}-{})", Thread.currentThread().getId(), start, end);
                for (int i = start; i <= end; i++) {
                    result += i;
                }
            } else {
                log.info("线程{}分治({}-{})", Thread.currentThread().getId(), start, end);
                int middle = (start + end) / 2;
                MyTask task1 = new MyTask(start, middle);
                MyTask task2 = new MyTask(middle + 1, end);
                task1.fork();
                task2.fork();
                result = task1.join() + task2.join();
            }
            return result;
        }
    }
}
