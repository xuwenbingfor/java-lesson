package com.jz.java.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author xuwenbingfor
 * @version 2020/5/31 8:14
 * @description
 */
@Slf4j
public class ChannelTests {
    @Test
    public void test0() throws IOException {
        RandomAccessFile aFile = new RandomAccessFile("F:\\1.txt", "rw");
        FileChannel inChannel = aFile.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(48);

        int bytesRead = inChannel.read(buf);
        while (bytesRead != -1) {

            System.out.println("Read " + bytesRead);
            buf.flip();

            while (buf.hasRemaining()) {
                System.out.print((char) buf.get());
            }

            buf.clear();
            bytesRead = inChannel.read(buf);
        }
        aFile.close();
    }

    @Test
    public void test1() throws IOException {
        LocalDateTime start = LocalDateTime.now();
        File in = new File("E:\\安装包\\ubuntu-16.04.4-desktop-amd64.iso");
        File out = new File("F:\\ubuntu-16.04.4-desktop-amd64.iso");
//        FileInputStream fileInputStream = new FileInputStream(in);
//        FileOutputStream fileOutputStream = new FileOutputStream(out);
        FileCopyUtils.copy(in, out);
        LocalDateTime end = LocalDateTime.now();
        // 1.5G 60s
        log.info("io 文件拷贝总耗时：{} s", Duration.between(start, end).toSeconds());
    }

    @Test
    public void test2() throws IOException {
        LocalDateTime start = LocalDateTime.now();
        File in = new File("E:\\安装包\\ubuntu-16.04.4-desktop-amd64.iso");
        File out = new File("F:\\ubuntu-16.04.4-desktop-amd64.iso");
        FileInputStream fileInputStream = new FileInputStream(in);
        FileOutputStream fileOutputStream = new FileOutputStream(out);

        FileChannel inChannel = fileInputStream.getChannel();
        FileChannel outChannel = fileOutputStream.getChannel();

        // 1.5G 30s
//        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        // 1.5G 30s
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);

        while (inChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            outChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        inChannel.close();
        outChannel.close();
        fileInputStream.close();
        fileOutputStream.close();
        LocalDateTime end = LocalDateTime.now();

        log.info("nio ByteBuffer文件拷贝总耗时：{} s", Duration.between(start, end).toSeconds());
    }


    @Test
    public void test3() throws IOException {
        LocalDateTime start = LocalDateTime.now();
        FileChannel inChannel = FileChannel.open(
                Paths.get("E:\\安装包\\ubuntu-16.04.4-desktop-amd64.iso"),
                StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(
                Paths.get("F:\\ubuntu-16.04.4-desktop-amd64.iso"),
                StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        MappedByteBuffer inMappedByteBuffer = inChannel
                .map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedByteBuffer = outChannel
                .map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        byte[] bytes = new byte[inMappedByteBuffer.limit()];
        inMappedByteBuffer.get(bytes);
        outMappedByteBuffer.put(bytes);

        inChannel.close();
        outChannel.close();
        LocalDateTime end = LocalDateTime.now();

        // 1.5G 9s
        log.info("nio 文件拷贝总耗时：{} s", Duration.between(start, end).toSeconds());
    }


    @Test
    public void test4() throws IOException {
        LocalDateTime start = LocalDateTime.now();
        FileChannel inChannel = FileChannel.open(
                Paths.get("E:\\安装包\\ubuntu-16.04.4-desktop-amd64.iso"),
                StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(
                Paths.get("F:\\ubuntu-16.04.4-desktop-amd64.iso"),
                StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inChannel.close();
        outChannel.close();
        LocalDateTime end = LocalDateTime.now();

        // 1.5G 30s
        log.info("nio transferTo文件拷贝总耗时：{} s", Duration.between(start, end).toSeconds());
    }
}
