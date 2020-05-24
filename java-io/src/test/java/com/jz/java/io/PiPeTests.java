package com.jz.java.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author xuwenbingfor
 * @version 2020/5/24 21:41
 * @description
 */
@Slf4j
public class PiPeTests {
    final PipedOutputStream outputStream = new PipedOutputStream();
    final PipedInputStream inputStream = new PipedInputStream(outputStream);

    // java.io.IOException: Already connected
//    final PipedInputStream inputStream3 = new PipedInputStream(outputStream);

    public PiPeTests() throws IOException {
    }

    @Test
    public void test1() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            try {
                outputStream.write("abcd".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                int data = inputStream.read();
                while (data != -1) {
                    log.info("data:{}", (char) data);
                    data = inputStream.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        Thread thread3 = new Thread(() -> {
//            try {
//                int data = inputStream3.read();
//                while (data != -1) {
//                    log.info("data:{}", (char) data);
//                    data = inputStream3.read();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        thread1.start();
        thread2.start();
//        thread3.start();
        TimeUnit.SECONDS.sleep(1);
    }
}
