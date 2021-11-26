package com.jz.java.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author xuwenbingfor
 * @version 2020/5/30 21:37
 * @description
 */
@Slf4j
public class BufferTests {
    @Test
    public void test3() {
        int n = 5;
        ByteBuffer buffer = ByteBuffer.allocate(n);
        buffer.put((byte) 5);
        buffer.put((byte) 8);
        buffer.put((byte) 3);
        System.out.println("The Original ByteBuffer is: " + Arrays.toString(buffer.array()));
        System.out.println("The position is: " + buffer.position());
        System.out.println("The limit is: " + buffer.limit());
        ByteBuffer bufferCompact = buffer.compact();
        System.out.println("\nThe Compacted ByteBuffer is: " + Arrays.toString(bufferCompact.array()));
        System.out.println("The position is: " + bufferCompact.position());
        System.out.println("The limit is: " + bufferCompact.limit());
    }

    @Test
    public void test2() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());
        byte[] bytes = new byte[10];
        byteBuffer.put(bytes);
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());
    }

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
        log.info("read : {}", (char) byteBuffer.get());
        log.info("position:{}", byteBuffer.position());
        log.info("limit:{}", byteBuffer.limit());
        log.info("capacity:{}", byteBuffer.capacity());
    }
}
