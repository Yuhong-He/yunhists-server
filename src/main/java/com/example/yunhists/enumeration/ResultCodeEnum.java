package com.example.yunhists.enumeration;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(202, "失败"),
    LOGIN_AUTH(203, "未登陆"),
    PERMISSION(204, "没有权限"),
    LOGIN_ERROR(207, "邮箱或密码错误"),
    EMAIL_NOT_REGISTERED(208, "邮箱未注册"),
    REGISTERED_WITH_GOOGLE(209, "谷歌注册用户"),

    SECKILL_ILLEGAL(217, "请求不合法"),
    LOGIN_CODE(222,"长时间未操作,会话已失效,请刷新页面后重试!"),
    CODE_ERROR(223,"验证码错误!"),
    TOKEN_ERROR(224,"Token无效!")
    ;

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
