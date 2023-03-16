package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class STSUtilsTest {

    @Test
    public void getSTS() {
        Map<String, String> map = STSUtils.getSTS(1);
        assert map != null;
        assertTrue(map.get("accessKeyId").length() > 0);
        assertTrue(map.get("accessKeySecret").length() > 0);
        assertTrue(map.get("stsToken").length() > 0);
    }

}
