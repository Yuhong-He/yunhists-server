package com.example.yunhists.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    public static String decodeUrl(String url)
    {
        String prevURL = "";
        String decodeURL = url;
        while(!prevURL.equals(decodeURL)) {
            prevURL = decodeURL;
            decodeURL = URLDecoder.decode( decodeURL, StandardCharsets.UTF_8);
        }
        return decodeURL;
    }

}
