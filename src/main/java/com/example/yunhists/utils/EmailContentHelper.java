package com.example.yunhists.utils;

public class EmailContentHelper {

    public static String getRegisterVerificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "注册验证码";
        } else {
            return "Register Verification Code";
        }
    }

    public static String getResetPasswordEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "重置密码";
        } else {
            return "Reset Password";
        }
    }

    public static String getChangeEmailVerificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "邮箱验证码";
        } else {
            return "Email Verification Code";
        }
    }

    public static String getRegisterVerificationEmailBody(String lang, String code) {
        if(lang.equals("zh")) {
            return "<p>新用户您好，这是您的注册验证码：</p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>验证码五分钟内有效，请尽快验证。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Welcome, this is your verification code: </p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>The verification code has 5 minutes expiration. Please verify as soon as possible.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getResetPasswordEmailBody(String lang, String username, String password) {
        if(lang.equals("zh")) {
            return "<p>" + username + "您好，这是您的新密码：</p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + password + "</p>" +
                    "<p>请使用新密码登录，然后在<a href=\"https://www.yunnanhistory.com/profile\">您的用户页</a>修改密码，请不要将密码透露给他人。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Hello " + username + ", this is your new password: </p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + password + "</p>" +
                    "<p>Please login with the new password, then change password at <a href=\"https://www.yunnanhistory.com/profile\">your profile page</a>. Please do not disclose your password to others.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

    public static String getChangeEmailVerificationEmailBody(String lang, String username, String code) {
        if(lang.equals("zh")) {
            return "<p>" + username + "您好，这是您的改绑邮箱验证码：</p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>验证码五分钟内有效，请尽快验证。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Hello " + username + ", this is your change email verification code: </p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>The verification code has 5 minutes expiration. Please verify as soon as possible.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

}
