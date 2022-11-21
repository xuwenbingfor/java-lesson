package com.jz.java.collection.util;

import cn.hutool.json.JSONUtil;
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
        log.info("{}: {}", name, JSONUtil.toJsonStr(object));
    }

    /**
     * 打印Collection
     *
     * @param collection
     */
    public static void print(Collection collection) {
        String name = collection.getClass().getName();
        log.info("{} size: {}", name, collection.size());
        log.info("{}: {}", name, JSONUtil.toJsonStr(collection));
    }
}
