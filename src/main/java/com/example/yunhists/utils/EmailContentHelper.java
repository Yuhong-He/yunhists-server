package com.example.yunhists.utils;

public class EmailContentHelper {

    public static String getRegisterVerificationEmailSubject(String lang) {
        if(lang.equals("zh")) {
            return "注册验证码";
        } else {
            return "Register Verification Code";
        }
    }

    public static String getRegisterVerificationEmailBody(String lang, String code) {
        if(lang.equals("zh")) {
            return "<p>新用户您好，这是您的注册验证码：</p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>验证码五分钟内有效，仅可使用一次，请尽快验证。</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        } else {
            return "<p>Welcome, this is your verification code: </p>" +
                    "<p style='text-align:center; font-weight: bold;'>" + code + "</p>" +
                    "<p>The verification code has 5 minutes expiration and can only use once. Please verify as soon as possible.</p>" +
                    "<p>滇史论辑 Yunhists</p>";
        }
    }

}
