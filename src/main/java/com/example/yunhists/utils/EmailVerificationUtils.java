package com.example.yunhists.utils;

import com.example.yunhists.entity.EmailVerification;

import java.util.Date;
import java.util.Random;

public class EmailVerificationUtils {

    public static EmailVerification createVerification(String email) {
        Random random = new Random();
        StringBuilder verificationCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            verificationCode.append(random.nextInt(10));
        }
        return new EmailVerification(email, String.valueOf(verificationCode));
    }

    public static boolean isExpiration(EmailVerification ev) {
        long currentTime = new Date().getTime();
        long fiveMinutes = 5 * 60 * 1000;
        return currentTime - ev.getTimestamp().getTime() > fiveMinutes;
    }

}
