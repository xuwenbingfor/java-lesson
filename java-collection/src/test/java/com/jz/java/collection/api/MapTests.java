package com.jz.java.collection.api;

import com.jz.java.collection.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MapTests {
    @Test
    public void test1() {
        Map<String, String> map = new HashMap<>();
        // HashMap允许key==null
        map.put(null, "hello1");
        map.put(null, "hello2");
        log.info("{}", JsonUtil.toJson(map));
    }
}
