package com.example.yunhists.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomPage {
    private Object records;
    private long total;
    private long size;
    private long current;
    private long pages;
}
