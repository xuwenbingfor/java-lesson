package com.jz.java.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * @author xuwenbingfor
 * @version 2020/5/26 23:40
 * @description
 */
@Slf4j
public class StreamTokenizerTests {
    @Test
    public void test1() throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader("Mary had 1 little lamb...\nabc"));

        log.info("start");
        // 译者注：TT_EOF表示流末尾，TT_EOL表示行末尾。
        while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
            if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                log.info("TT_WORD:{}", tokenizer.sval);
            } else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                log.info("TT_NUMBER:{}", tokenizer.nval);
            } else if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
                log.info("TT_EOL");
            } else {
                log.info("{}", tokenizer.ttype);
            }
        }
        log.info("end");
    }
}
