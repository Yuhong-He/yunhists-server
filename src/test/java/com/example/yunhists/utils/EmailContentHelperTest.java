package com.example.yunhists.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailContentHelperTest {

    @Test
    public void getRegisterVerificationEmailSubject() {
        assertTrue(EmailContentHelper.getRegisterVerificationEmailSubject("zh").contains("注册验证码"));
        assertTrue(EmailContentHelper.getRegisterVerificationEmailSubject("en").contains("Register"));
    }

    @Test
    public void getRegisterVerificationEmailBody() {
        assertTrue(EmailContentHelper.getRegisterVerificationEmailBody("zh", "").contains("新用户您好"));
        assertTrue(EmailContentHelper.getRegisterVerificationEmailBody("en", "").contains("Welcome"));
    }

}
