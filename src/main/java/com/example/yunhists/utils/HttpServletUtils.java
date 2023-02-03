package com.example.yunhists.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class HttpServletUtils {

    public static String getToken(HttpServletRequest request) {
        String token = "";
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            if(key.equals("token")) {
                token = request.getHeader(key);
                break;
            }
        }
        return token;
    }

}
