package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@TableName("delThesis")
public class DelThesis {

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @TableField("author")
    private String author;

    @TableField("title")
    private String title;

    @TableField("publication")
    private String publication;

    @TableField("location")
    private String location;

    @TableField("publisher")
    private String publisher;

    @TableField("year")
    private Integer year;

    @TableField("volume")
    private Integer volume;

    @TableField("issue")
    private String issue;

    @TableField("pages")
    private String pages;

    @TableField("doi")
    private String doi;

    @TableField("isbn")
    private String isbn;

    @TableField("online_publisher")
    private String onlinePublisher;

    @TableField("online_publish_url")
    private String onlinePublishUrl;

    @TableField("type")
    private Integer type;

    @TableField("copyright_status")
    private Integer copyrightStatus;

    @TableField("file_name")
    private String fileName;

    @TableField("uploader")
    private Integer uploader;

    @TableField("approver")
    private Integer approver;

    @TableField("approve_time")
    private Timestamp approveTime;

    @TableField("del_operator")
    private Integer delOperator;

    @TableField("del_time")
    private Timestamp delTime;

    public DelThesis(Thesis thesis, String file, int userId) {
        this.author = thesis.getAuthor();
        this.title = thesis.getTitle();
        this.publication = thesis.getPublication();
        this.location = thesis.getLocation();
        this.publisher = thesis.getPublisher();
        this.year = thesis.getYear();
        this.volume = thesis.getVolume();
        this.issue = thesis.getIssue();
        this.pages = thesis.getPages();
        this.doi = thesis.getDoi();
        this.isbn = thesis.getIsbn();
        this.onlinePublisher = thesis.getOnlinePublisher();
        this.onlinePublishUrl = thesis.getOnlinePublishUrl();
        this.type = thesis.getType();
        this.copyrightStatus = thesis.getCopyrightStatus();
        this.fileName = file;
        this.uploader = thesis.getUploader();
        this.approver = thesis.getApprover();
        this.approveTime = thesis.getApproveTime();
        this.delOperator = userId;
    }
}
