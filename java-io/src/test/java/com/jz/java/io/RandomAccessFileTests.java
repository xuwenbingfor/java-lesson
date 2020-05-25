package com.jz.java.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author xuwenbingfor
 * @version 2020/5/25 22:52
 * @description
 */
@Slf4j
public class RandomAccessFileTests {
    @Test
    public void test() throws IOException {
        RandomAccessFile file = new RandomAccessFile("C:/Users/xwb/Desktop/1.txt", "rw");
        file.seek(21);
        log.info("start pointer:{}", file.getFilePointer());
        int read = file.read();
        log.info("start data:{}", (char) read);
        while (read != -1) {
//            file.seek(file.getFilePointer() + 1);
            log.info("pointer:{}", file.getFilePointer());
            read =  file.read();
            log.info("data:{}", (char) read);
        }
        file.close();
    }
}
