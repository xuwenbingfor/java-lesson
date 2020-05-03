package com.jz.java.collection.util;

import com.jz.java.collection.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class PrintUtils {
    /**
     * 打印Object
     *
     * @param object
     */
    public static void print(Object object) {
        String name = object.getClass().getName();
        log.info("{}: {}", name, JsonUtil.toJson(object));
    }

    /**
     * 打印Collection
     *
     * @param collection
     */
    public static void print(Collection collection) {
        String name = collection.getClass().getName();
        log.info("{} size: {}", name, collection.size());
        log.info("{}: {}", name, JsonUtil.toJson(collection));
    }
}
