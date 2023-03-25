package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@TableName("upload")
public class Upload extends Thesis {

    @TableField("category")
    private String category;

    @TableField("new_category")
    private String newCategory;

    @TableField("upload_time")
    private Timestamp uploadTime;

    @TableField("status")
    private Integer status;

    public Upload(Thesis thesis, int userId, String categories) {
        super(thesis.getAuthor(), thesis.getTitle(), thesis.getPublication(), thesis.getLocation(), thesis.getPublisher(),
                thesis.getYear(), thesis.getVolume(), thesis.getIssue(), thesis.getPages(), thesis.getDoi(), thesis.getIsbn(),
                thesis.getOnlinePublisher(), thesis.getOnlinePublishUrl(), thesis.getType(), thesis.getCopyrightStatus(),
                thesis.getFileName(), userId, userId, Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(new Date().getTime()))));
        this.category = categories;
        this.newCategory = "";
        this.status = 1;
    }
}
