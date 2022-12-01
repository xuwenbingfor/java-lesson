package com.jz.es.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SugInitServiceTest {
    @Autowired
    private SugInitService sugInitService;

    @Test
    void startProcess() {
        sugInitService.startProcess();
    }

    @Test
    void startIndex() {
        sugInitService.startIndex();
    }
}