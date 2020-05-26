package com.jz.java.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * @author xuwenbingfor
 * @version 2020/5/26 22:59
 * @description
 */
@Slf4j
public class PushbackInputStreamTests {
    @Test
    public void test1() throws IOException {
        PushbackInputStream input = new PushbackInputStream(
                new FileInputStream("C:\\Users\\xwb\\Desktop\\1.txt"), 8);
        // java.io.IOException: Push back buffer is full
//        byte[] bytes = new byte[10];
        byte[] bytes = new byte[8];
        input.read(bytes);
        log.info("1:{}", JsonUtil.toJson(bytes));
        input.unread(bytes);
        input.read(bytes);
        log.info("2:{}", JsonUtil.toJson(bytes));
        input.read(bytes);
        log.info("3:{}", JsonUtil.toJson(bytes));
    }

}
