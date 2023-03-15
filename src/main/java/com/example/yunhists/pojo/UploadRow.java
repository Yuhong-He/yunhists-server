package com.example.yunhists.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadRow {

    private Integer id;
    private String author;
    private String title;
    private String publication;
    private ThesisIssue thesisIssue;
    private String uploader;
    private Integer status;
    private String approver;

}
