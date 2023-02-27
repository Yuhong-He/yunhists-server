package com.example.yunhists.enumeration;

import lombok.Getter;

@Getter
public enum CategoryEnum {

    TYPE_LINK_THESIS(0),
    TYPE_LINK_CATEGORY(1),
    CHILD_CAT_NOT_EXIST(1),
    PARENT_CAT_NOT_EXIST(2),
    CAN_NOT_ADD_CAT_TO_ITSELF(3),
    CATEGORY_LINK_EXIST(4),
    THESIS_NOT_EXIST(5),
    MOVE_TO_ITSELF(6);

    private final Integer code;

    CategoryEnum(Integer code) {
        this.code = code;
    }
}
