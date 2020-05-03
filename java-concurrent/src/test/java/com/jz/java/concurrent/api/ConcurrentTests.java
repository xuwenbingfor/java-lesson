package com.jz.java.concurrent.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ConcurrentTests {
    @Test
    public void test1() {
        // 获取逻辑处理器个数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        log.info("availableProcessors:{}", availableProcessors);
    }

    @Test
    public void test2() throws InterruptedException {
        //
        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                int index = 0;
                while (true) {
                    index++;
                    if (index == 10000) {
                        index = 0;
                        // 单线程死循环[有log.info,无Thread.sleep]平均cpu占90%
                        // 单线程死循环[无log.info,无Thread.sleep]平均cpu占40%
                        // 单线程死循环[有log.info,有Thread.sleep]平均cpu占3%
                        log.info("线程：{}计数1w次", Thread.currentThread().getId());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        TimeUnit.MINUTES.sleep(1);
    }
}
