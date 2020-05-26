package com.jz.java.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * @author xuwenbingfor
 * @version 2020/5/26 23:26
 * @description
 */
@Slf4j
public class LineNumberReaderTests {
    @Test
    public void test1() throws IOException {
        LineNumberReader reader = new LineNumberReader(new FileReader("C:\\Users\\xwb\\Desktop\\1.txt"));

//        reader.setLineNumber(2);
        int data = reader.read();
        int lineNumber = reader.getLineNumber();
        while (data != -1) {
            char dataChar = (char) data;
            log.info("dataChar:{},lineNumber:{}", dataChar, lineNumber);

            data = reader.read();
            lineNumber = reader.getLineNumber();
        }
    }
}
