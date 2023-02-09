package com.example.yunhists.entity;

import lombok.Data;

@Data
public class CategoryName {

    private Integer id;

    private String zhName;

    private String enName;

    public CategoryName(Integer id, String zhName, String enName) {
        this.id = id;
        this.zhName = zhName;
        this.enName = enName;
    }

}
