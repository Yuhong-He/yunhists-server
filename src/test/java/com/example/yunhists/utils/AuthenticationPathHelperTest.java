package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationPathHelperTest {

    @Test
    public void authenticationPathHelperTest() {
        assertEquals(17, AuthenticationPathHelper.unregistered().length);
        assertEquals(10, AuthenticationPathHelper.banned().length);
        assertEquals(9, AuthenticationPathHelper.user().length);
        assertEquals(20, AuthenticationPathHelper.admin().length);
    }

}
