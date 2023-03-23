package com.example.yunhists.enumeration;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"Success"),
    FAIL(201, "Fail"),
    NOT_REGISTERED_API(202, "Not Registered API"),

    // User
    NO_PERMISSION(203, "No Permission"),
    INVALID_LANG(204, "Language not support"),
    NO_USER(205, "User Not Exist"),
    PASSWORD_INCORRECT(206, "Incorrect Password"),
    PASSWORD_NOT_MATCH(207, "Password not match"),
    EMAIL_NOT_REGISTERED(208, "Email Not Registered"),
    REGISTERED_WITH_GOOGLE(209, "Google Registered Account"),
    INVALID_EMAIL(210, "Invalid Email Address"),
    LESS_THAN_ONE_MINUTE(211, "Please wait one minute"),
    EMAIL_FAIL(212, "Email send fail"),
    VERIFY_CODE_EXPIRED(213, "Verification code expired"),
    VERIFY_CODE_INCORRECT(214, "Verification code incorrect"),
    EMAIL_ALREADY_REGISTERED(215, "Email already registered"),
    USERNAME_LENGTH(216, "Username length incorrect"),
    PASSWORD_LENGTH(217, "Password length incorrect"),
    NO_VERIFICATION_CODE(218, "Verification code does not send before"),
    INVALID_PARAM(219, "Email Notification Status Invalid"),
    TOKEN_EXPIRED(223,"Token Expired"),
    TOKEN_ERROR(224,"Invalid Token"),
    MISS_TOKEN(225,"Bad Request: Token Missing"),

    // Category
    REPEAT_ZH_NAME(301,"Category Chinese name exist"),
    REPEAT_EN_NAME(302,"Category English name exist"),
    PARENT_CAT_NOT_EXIST(303, "One of parent category not exist"),
    EMPTY_SEARCH(304, "Empty Search"),
    CATEGORY_ID_NOT_EXIST(305, "Category ID not exist"),
    CAN_ONLY_DELETE_EMPTY_CAT(306, "Can only delete empty category"),

    // Thesis
    NO_MORE_DOWNLOAD(401, "You can't download more today"),
    INVALID_TYPE(402, "Invalid type"),
    INVALID_COPYRIGHT(403, "Invalid copyright"),
    THESIS_FILE_MISSING(404, "Thesis file missing"),
    THESIS_EXIST(406, "Thesis exist"),
    THESIS_ID_NOT_EXIST(407, "Thesis id not exist"),
    THESIS_FILE_NOT_EXIST(408, "Thesis file not exist"),

    // Upload
    UPLOAD_ID_NOT_EXIST(501, "Upload id not exist"),
    NOT_YOUR_UPLOAD(502, "This is not the thesis you uploaded"),
    UPLOAD_CAN_NOT_UPDATE(503, "This upload can not be change"),
    UPLOAD_FILE_NOT_EXIST(504, "Uploaded thesis file not exist"),
    MOVE_UPLOAD_FILE_FAIL(201, "Move uploaded thesis file failed"),
    REMOVE_FILE_FAIL(201, "Remove file failed"),
    ;

    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
