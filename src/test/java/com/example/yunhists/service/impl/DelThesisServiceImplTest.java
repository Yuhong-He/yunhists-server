package com.example.yunhists.service.impl;

import com.example.yunhists.service.DelThesisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DelThesisServiceImplTest {

    @Autowired
    DelThesisService delThesisService;

    @Test
    public void validateChineseName() {
        assertNull(delThesisService.getThesisByFile("abc"));
    }

}
