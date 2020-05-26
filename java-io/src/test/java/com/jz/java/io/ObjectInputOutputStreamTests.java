package com.jz.java.io;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * @author xuwenbingfor
 * @version 2020/5/26 22:34
 * @description
 */
@Slf4j
public class ObjectInputOutputStreamTests {
    @Test
    public void test1() throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("C:\\Users\\xwb\\Desktop\\1.txt"));
        Person object = new Person();
        object.setAge(100);
        object.setName("夏雪宜");
        // java.io.NotSerializableException
        output.writeObject(object);
        output.close();
    }


    @Test
    public void test2() throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(new FileInputStream("C:\\Users\\xwb\\Desktop\\1.txt"));
        Person object = (Person) input.readObject();
        log.info("person:{}", object);
        input.close();
    }


    @Data
    @ToString
    public static class Person implements Serializable {
        private String name;
        private Integer age;
    }
}
