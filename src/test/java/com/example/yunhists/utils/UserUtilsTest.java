package com.example.yunhists.utils;

import com.example.yunhists.service.ThesisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UserUtilsTest {

    @Test
    public void testAllMethodsInUserUtils() {
        assertFalse(UserUtils.validateEmail("aaa"));
        assertFalse(UserUtils.validateUsername("a"));
        assertFalse(UserUtils.validatePassword("aaa"));
        assertFalse(UserUtils.validateLang("fr"));
        assertEquals(10, UserUtils.generateRandomPwd().length());
    }

}
