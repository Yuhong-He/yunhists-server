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

}
