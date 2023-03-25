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
@TableName("thesis")
public class Thesis {

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

    public Thesis(Upload upload, int uploaderId, int adminId) {
        this.author = upload.getAuthor();
        this.title = upload.getTitle();
        this.publication = upload.getPublication();
        this.location = upload.getLocation();
        this.publisher = upload.getPublisher();
        this.year = upload.getYear();
        this.volume = upload.getVolume();
        this.issue = upload.getIssue();
        this.pages = upload.getPages();
        this.doi = upload.getDoi();
        this.isbn = upload.getIsbn();
        this.onlinePublisher = upload.getOnlinePublisher();
        this.onlinePublishUrl = upload.getOnlinePublishUrl();
        this.type = upload.getType();
        this.copyrightStatus = upload.getCopyrightStatus();
        this.fileName = upload.getFileName();
        this.uploader = uploaderId;
        this.approver = adminId;
    }

    public Thesis(String author, String title, String publication, String location, String publisher, Integer year,
                  Integer volume, String issue, String pages, String doi, String isbn, String onlinePublisher,
                  String onlinePublishUrl, Integer type, Integer copyrightStatus, String file, Integer uploader,
                  Integer approver, Timestamp approveTime) {

        this.author = author;
        this.title = title;
        this.publication = publication;
        this.location = location;
        this.publisher = publisher;
        this.year = year;
        this.volume = volume;
        this.issue = issue;
        this.pages = pages;
        this.doi = doi;
        this.isbn = isbn;
        this.onlinePublisher = onlinePublisher;
        this.onlinePublishUrl = onlinePublishUrl;
        this.type = type;
        this.copyrightStatus = copyrightStatus;
        this.fileName = file;
        this.uploader = uploader;
        this.approver = approver;
        this.approveTime = approveTime;
    }
}
