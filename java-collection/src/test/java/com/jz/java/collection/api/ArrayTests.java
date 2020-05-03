package com.jz.java.collection.api;

import com.jz.java.collection.model.Book;
import com.jz.java.collection.model.Person;
import com.jz.java.collection.util.PrintUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
public class ArrayTests {
    @Test
    public void test1() {
        Integer[] array = new Integer[]{1, 2, 3};
//        List<Integer> list = Arrays.asList(array);
//        PrintUtils.print(list);
//        array[0] = 100;
//        PrintUtils.print(list);
//        list.add(101);
    }

    @Test
    public void test2() {
        Book book1 = Book.builder().id(1).name("飘").build();
        Person person1 = Person.builder()
                .id(10)
                .name("xuwenbingfor")
                .age(27)
                .books(Collections.singletonList(book1))
                .build();
        Person[] persons1 = new Person[]{person1};
        // copyOf仅仅将对象地址放入数组
        Person[] persons2 = Arrays.copyOf(persons1, 1);
        PrintUtils.print(persons2);
        log.info("{}", persons1 == persons2);
        log.info("{}", persons1[0] == persons2[0]);
        log.info("{}", persons1[0].getBooks() == persons2[0].getBooks());
    }

    @Test
    public void test3() {
        Book book1 = Book.builder().id(1).name("飘").build();
        Book[] books1 = new Book[]{book1};
        Book[] books2 = new Book[1];
        System.arraycopy(books1, 0, books2, 0, 1);
        log.info("{}", books1 == books2);//false
        log.info("{}", books1[0] == books2[0]);//true
    }
}
