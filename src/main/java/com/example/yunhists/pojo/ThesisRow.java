package com.example.yunhists.pojo;

import com.example.yunhists.pojo.CategoryName;
import com.example.yunhists.pojo.ThesisIssue;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ThesisRow {

    private Integer id;
    private String author;
    private String title;
    private String publication;
    private ThesisIssue thesisIssue;
    private List<CategoryName> categories;

}
