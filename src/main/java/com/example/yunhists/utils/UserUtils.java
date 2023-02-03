package com.example.yunhists.utils;

import java.util.regex.Pattern;

public class UserUtils {

    public static boolean validateEmail(String email) {
        String regex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        return Pattern.matches(regex, email);
    }

    public static boolean validateUsername(String username) {
        return username.length() > 2 && username.length() < 15;
    }

    public static boolean validatePassword(String pwd) {
        return pwd.length() >= 6;
    }

    public static boolean validateConfirmPassword(String pwd, String pwd2) {
        return pwd.equals(pwd2);
    }

}
