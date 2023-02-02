package com.example.yunhists.enumeration;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(202, "失败"),
    LOGIN_AUTH(203, "未登陆"),
    PERMISSION(204, "没有权限"),
    LOGIN_ERROR(207, "密码错误"),
    EMAIL_NOT_REGISTERED(208, "邮箱未注册"),
    REGISTERED_WITH_GOOGLE(209, "谷歌注册用户"),
    TOKEN_ERROR(224,"Token无效!")
    ;

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
