package com.jz.java.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @author xuwenbingfor
 * @version 2020/5/30 21:37
 * @description
 */
@Slf4j
public class BufferTests {
    @Test
    public void test1() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        log.info("--------- allocate -----------");
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());
        log.info("--------- put abc -----------");
        String abc = "abc";
        byteBuffer.put(abc.getBytes());
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());
        log.info("--------- flip(切换到读数据模式) -----------");
        byteBuffer.flip();
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());

        log.info("--------- get -----------");
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        log.info("read : {}", new String(bytes));
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());

        log.info("--------- rewind(可重复读) -----------");
        byteBuffer.rewind();
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());

        log.info("--------- clear(清空缓冲，但数据不清空，数据此时是被遗忘状态) -----------");
        byteBuffer.clear();
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());

        log.info("--------- get for test -----------");
        log.info("read : {}", (char)byteBuffer.get());
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());
    }
}
