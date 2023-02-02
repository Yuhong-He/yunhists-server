package com.example.yunhists.utils;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JwtHelperTest {

    Long userId = 1L;
    Integer userRights = 0;
    static String testToken = "";

    @Order(1)
    @Test
    public void createToken_anyData_success() {
        String token = JwtHelper.createToken(userId, userRights);
        assertTrue(token.length() > 0);
        testToken = token;
    }

    @Order(2)
    @Test
    public void getUserId_validToken_success() {
        Long result = JwtHelper.getUserId(testToken);
        assertEquals(userId, result);
    }

    @Order(3)
    @Test
    public void getUserRights_validToken_success() {
        Integer result = JwtHelper.getUserRights(testToken);
        assertEquals(userRights, result);
    }

    @Order(4)
    @Test
    public void isExpiration_validToken_success() {
        assertFalse(JwtHelper.isExpiration(testToken));
    }

}
