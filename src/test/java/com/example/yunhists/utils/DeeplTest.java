package com.example.yunhists.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DeeplTest {

    @Test
    public void translateToZh_fail() {
        assertEquals("DeepL Translation ERROR", DeepL.translateToZh(""));
    }

    @Test
    public void translateToZh_success() {
        String input = "Dublin";
        String output = "都柏林";
        assertEquals(output, DeepL.translateToZh(input));
    }

    @Test
    public void translateToEn_success() {
        String input = "都柏林";
        String output = "Dublin";
        assertEquals(output, DeepL.translateToEn(input));
    }

    @Test
    public void translateToEn_fail() {
        assertEquals("DeepL Translation ERROR", DeepL.translateToEn(""));
    }

}
