package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionHelperTest {

    @Test
    public void authenticationPathHelperTest() {
        assertEquals(18, PermissionHelper.unregistered().length);
        assertEquals(10, PermissionHelper.banned().length);
        assertEquals(8, PermissionHelper.user().length);
        assertEquals(20, PermissionHelper.admin().length);
    }

}
