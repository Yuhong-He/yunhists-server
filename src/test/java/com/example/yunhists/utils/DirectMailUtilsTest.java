package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectMailUtilsTest {

    @Test
    public void sendEmail() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        DirectMailUtils.sendEmail("test@yunnanhistory.com", "DirectMailUtilsTest", "This is the test email from Junit test - DirectMailUtilsTest");
        assertTrue(outContent.toString().contains("Send email: "));
    }

}
