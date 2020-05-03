package com.jz.java.lang;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class RuntimeTests {
    @Test
    public void test1() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("jvm exit");
        }));
    }
}
