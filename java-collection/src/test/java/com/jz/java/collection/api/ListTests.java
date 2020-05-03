package com.jz.java.collection.api;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class ListTests {
    @Test
    public void test2() {

    }
    @Test
    public void test1() {
        List<Integer> list = Lists.newArrayList();
        // java13 返回Object[]
        Object[] objects = list.toArray();
    }
}
