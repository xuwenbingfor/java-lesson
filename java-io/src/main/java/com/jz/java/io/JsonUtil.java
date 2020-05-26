package com.jz.java.io;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author:administer
 * @description :
 * @version:2018-10-17 8:29
 */
@Slf4j
public class JsonUtil {
    /**
     * 将json字符串转换成Object
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isNotBlank(text)){
            T t = JSONObject.parseObject(text, clazz);
            return t;
        }else {
            return null;
        }
    }

    /**
     * 将json字符串转换成Map
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String,T> parseMap(String text, Class<T> clazz){
        if (StringUtils.isNotBlank(text)){
            Map<String,T> map = JSONObject.parseObject(text,new TypeReference<Map<String,T>>(){});
            return map;
        }else {
            return null;
        }
    }

    /**
     * 将json字符串转换成List
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseList(String text, Class<T> clazz) {
        if (StringUtils.isNotBlank(text)){
          List<T> ts = JSONArray.parseArray(text, clazz);
          return ts;
        }else {
            return null;
        }
    }

    /**
     * 将Object转换成json字符串
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String toJson(T t) {
        if (t!=null){
            return JSONObject.toJSONString(t);
        }else {
            return null;
        }
    }
}
