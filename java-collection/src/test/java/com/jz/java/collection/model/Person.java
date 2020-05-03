package com.jz.java.collection.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Person {
    private Integer id;
    private String name;
    private Integer age;
    private List<Book> books;
}
