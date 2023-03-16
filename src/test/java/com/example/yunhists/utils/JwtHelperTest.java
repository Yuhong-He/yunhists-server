package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtHelperTest {

    @Test
    public void getUserId_success() {
        int userId = 1;
        String access_token = JwtHelper.createAccessToken((long) userId);
        assertEquals(1, JwtHelper.getUserId(access_token));
    }

    @Test
    public void getUserId_fail() {
        assertNull(JwtHelper.getUserId("invalid_token"));
    }

    @Test
    public void notExpired_success() {
        int userId = 1;
        String refresh_token = JwtHelper.createRefreshToken((long) userId);
        assertTrue(JwtHelper.notExpired(refresh_token));
    }

    @Test
    public void notExpired_fail() {
        assertFalse(JwtHelper.notExpired("invalid_token"));
    }

    @Test
    public void getExpiredTimeTest() {
        assertTrue(JwtHelper.getExpiredTime() > System.currentTimeMillis());
    }

}
