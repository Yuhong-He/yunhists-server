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

    @Test
    public void getChangeEmailVerificationEmailSubject() {
        assertTrue(EmailContentHelper.getChangeEmailVerificationEmailSubject("zh").contains("邮箱验证码"));
        assertTrue(EmailContentHelper.getChangeEmailVerificationEmailSubject("en").contains("Email Verification Code"));
    }

    @Test
    public void getChangeEmailVerificationEmailBody() {
        assertTrue(EmailContentHelper.getChangeEmailVerificationEmailBody("zh", "tester", "123456").contains("tester您好，这是您的改绑邮箱验证码"));
        assertTrue(EmailContentHelper.getChangeEmailVerificationEmailBody("en", "tester", "123456").contains("Hello tester, this is your change email verification code"));
    }

}
