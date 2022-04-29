package com.jz.java.collection.api;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author xuwenbingfor
 * @version 2022/4/29 22:12
 * @description
 */
public class StreamTests {
    @Test
    public void testFlatMap() {
        // https://www.cnblogs.com/diegodu/p/8794857.html
        String[] arr1 = {"a", "b", "c", "d"};
        String[] arr2 = {"e", "f"};
        String[] arr3 = {"g", "h"};
        Stream.of(arr1, arr2, arr3)
                .flatMap(Arrays::stream)
                .forEach(System.out::println);
    }
}
