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

    @Test
    public void getDeleteThesisNotificationEmailSubject() {
        assertTrue(EmailContentHelper.getDeleteThesisNotificationEmailSubject("zh").contains("删除通知"));
        assertTrue(EmailContentHelper.getDeleteThesisNotificationEmailSubject("en").contains("Delete Notification"));
    }

    @Test
    public void getNewUploadNotificationEmailSubject() {
        assertTrue(EmailContentHelper.getNewUploadNotificationEmailSubject("zh").contains("新上传通知"));
        assertTrue(EmailContentHelper.getNewUploadNotificationEmailSubject("en").contains("New Upload Notification"));
    }

    @Test
    public void getUploadApprovedNotificationEmailSubject() {
        assertTrue(EmailContentHelper.getUploadApprovedNotificationEmailSubject("zh").contains("批准通知"));
        assertTrue(EmailContentHelper.getUploadApprovedNotificationEmailSubject("en").contains("Approved Notification"));
    }

    @Test
    public void getUploadRejectedNotificationEmailSubject() {
        assertTrue(EmailContentHelper.getUploadRejectedNotificationEmailSubject("zh").contains("驳回通知"));
        assertTrue(EmailContentHelper.getUploadRejectedNotificationEmailSubject("en").contains("Reject Notification"));
    }

    @Test
    public void getDeleteThesisNotificationEmailBody() {
        assertTrue(EmailContentHelper.getDeleteThesisNotificationEmailBody("zh", "user", "titleTest", "reasonTest", "admin").contains("user您好"));
        assertTrue(EmailContentHelper.getDeleteThesisNotificationEmailBody("en", "user", "titleTest", "reasonTest", "admin").contains("Hello user"));
        assertTrue(EmailContentHelper.getDeleteThesisNotificationEmailBody("en", "user", "titleTest", "", "admin").contains("<--- NULL --->"));
    }

    @Test
    public void getNewUploadNotificationEmailBody() {
        assertTrue(EmailContentHelper.getNewUploadNotificationEmailBody("zh").contains("管理员您好"));
        assertTrue(EmailContentHelper.getNewUploadNotificationEmailBody("en").contains("Dear admin"));
    }

    @Test
    public void getUploadApprovedNotificationEmailBody() {
        assertTrue(EmailContentHelper.getUploadApprovedNotificationEmailBody("zh", "user", "testTitle").contains("user您好"));
        assertTrue(EmailContentHelper.getUploadApprovedNotificationEmailBody("en", "user", "testTitle").contains("Dear user"));
    }

    @Test
    public void getUploadRejectedNotificationEmailBody() {
        assertTrue(EmailContentHelper.getUploadRejectedNotificationEmailBody("zh", "user", "testTitle", "testReason").contains("user您好"));
        assertTrue(EmailContentHelper.getUploadRejectedNotificationEmailBody("en", "user", "testTitle", "testReason").contains("Dear user"));
    }

}
