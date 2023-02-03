package com.example.yunhists.enumeration;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"Success"),
    FAIL(202, "Fail"),
    LOGIN_AUTH(203, "Not Login"),
    PERMISSION(204, "No Authentication"),
    NO_USER(205, "User Not Exist"),
    WRONG_PWD(207, "Wrong Password"),
    EMAIL_NOT_REGISTERED(208, "Email Not Registered"),
    REGISTERED_WITH_GOOGLE(209, "Google Registered Account"),
    TOKEN_EXPIRED(223,"Token Expired"),
    TOKEN_ERROR(224,"Invalid Token"),
    MISS_TOKEN(225,"Bad Request: Token Missing")
    ;

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
