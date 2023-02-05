package com.example.yunhists.utils;

import com.example.yunhists.entity.EmailTimer;

import java.util.Date;

public class EmailTimerUtils {

    public static boolean repeatEmail(EmailTimer emailTimer) {
        long currentTime = new Date().getTime();
        long oneMinute = 60 * 1000;
        return currentTime - emailTimer.getTimestamp().getTime() < oneMinute;
    }

}
