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

    @Test
    public void getResetPasswordEmailSubject() {
        assertTrue(EmailContentHelper.getResetPasswordEmailSubject("zh").contains("重置密码"));
        assertTrue(EmailContentHelper.getResetPasswordEmailSubject("en").contains("Reset Password"));
    }

    @Test
    public void getResetPasswordEmailBody() {
        assertTrue(EmailContentHelper.getResetPasswordEmailBody("zh", "test", "abcdef").contains("这是您的新密码"));
        assertTrue(EmailContentHelper.getResetPasswordEmailBody("en", "test", "abcdef").contains("this is your new password"));
    }

}
