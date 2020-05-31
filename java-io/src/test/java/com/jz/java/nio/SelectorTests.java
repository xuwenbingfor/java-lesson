package com.jz.java.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author xuwenbingfor
 * @version 2020/5/31 15:19
 * @description
 */
@Slf4j
public class SelectorTests {
    @Test
    public void test3() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            log.info("select 1");
            selector.selectedKeys().forEach(action -> {
                if (action.isAcceptable()) {
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer);
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.limit()];
                        byteBuffer.get(bytes);
                        log.info("接收到客户端请求数据：{}", new String(bytes));
                        socketChannel.close();
                        serverSocketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Test
    public void test1() throws IOException {
        SocketChannel socketChannel =
                SocketChannel.open(new InetSocketAddress("localhost", 8080));
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("hello selector".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();
        socketChannel.close();
    }

    @Test
    public void test2() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        SocketChannel socketChannel = serverSocketChannel.accept();
        log.info("waiting accept ok");
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes);
        log.info("接收到客户端请求数据：{}", new String(bytes));
        socketChannel.close();
        serverSocketChannel.close();
    }
}
