package com.example.yunhists.utils;

import com.example.yunhists.enumeration.ResultCodeEnum;

import javax.servlet.http.HttpServletRequest;

public class ControllerUtils {

    public static Object getUserIdFromToken(HttpServletRequest request) {
        String token = HttpServletUtils.getToken(request);
        if(!token.equals("")) {
            if(!JwtHelper.isExpiration(token)) {
                try{
                    Long userId = JwtHelper.getUserId(token);
                    assert userId != null;
                    return userId.intValue();
                } catch (Exception e) {
                    return Result.error(ResultCodeEnum.TOKEN_ERROR);
                }
            } else {
                return Result.error(ResultCodeEnum.TOKEN_EXPIRED);
            }
        } else {
            return Result.error(ResultCodeEnum.MISS_TOKEN);
        }
    }

    public static void printException(Exception e) {
        System.out.println(e.getMessage());
        for(StackTraceElement s : e.getStackTrace()) {
            System.out.println("\t" + s);
        }
    }

}
