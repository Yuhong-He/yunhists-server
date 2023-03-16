package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OSSUtilsTest {

    @Test
    public void moveFile_fail() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        OSSUtils.moveFile("aaa", "bbb");
        assertTrue(outContent.toString().contains("[ErrorCode]: NoSuchKey"));
    }

    @Test
    public void getAllFile_success() {
        List<String> list = OSSUtils.getAllFile();
        assert list != null;
        assertTrue(list.size() >= 1);
    }

}
