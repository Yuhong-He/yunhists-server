package com.example.yunhists.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryWithParentCat {

    private String zhName;

    private String enName;

    private List<Integer> parentCat;

}
