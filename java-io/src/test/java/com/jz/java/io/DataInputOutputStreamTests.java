package com.jz.java.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * @author xuwenbingfor
 * @version 2020/5/26 22:17
 * @description
 */
@Slf4j
public class DataInputOutputStreamTests {
    @Test
    public void test2() {
        try (DataInputStream inputStream =
                     new DataInputStream(new FileInputStream("C:\\Users\\xwb\\Desktop\\1.txt"))) {
            double doubleValue = inputStream.readDouble();
            char char1 = inputStream.readChar();
            char char2 = inputStream.readChar();
            char char3 = inputStream.readChar();
            log.info("{},{},{},{}", doubleValue, char1, char2, char3);
            // java.io.EOFException
            char char4 = inputStream.readChar();
            log.info("{}", char4);
            char char5 = inputStream.readChar();
            log.info("{}", char5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws FileNotFoundException {
        try (DataOutputStream dataOutputStream =
                     new DataOutputStream(new FileOutputStream("C:\\Users\\xwb\\Desktop\\1.txt"))) {
            dataOutputStream.writeDouble(100);
            dataOutputStream.writeChars("abc");
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
