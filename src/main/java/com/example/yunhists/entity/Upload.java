package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@TableName("upload")
public class Upload {

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

    @TableField("category")
    private String category;

    @TableField("new_category")
    private String newCategory;

    @TableField("uploader")
    private Integer uploader;

    @TableField("upload_time")
    private Timestamp uploadTime;

    @TableField("status")
    private Integer status;

    @TableField("approver")
    private Integer approver;

    @TableField("approve_time")
    private Timestamp approveTime;

    public Upload(Thesis thesis, int userId, String categories) {
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
        this.fileName = thesis.getFileName();
        this.category = categories;
        this.newCategory = "";
        this.uploader = userId;
        this.status = 1;
        this.approver = userId;
        this.approveTime = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(new Date().getTime())));
    }
}
