package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@TableName("delThesis")
public class DelThesis extends Thesis {

    @TableField("del_operator")
    private Integer delOperator;

    @TableField("del_time")
    private Timestamp delTime;

    public DelThesis(Thesis thesis, String file, int userId) {
        super(thesis.getAuthor(), thesis.getTitle(), thesis.getPublication(), thesis.getLocation(), thesis.getPublisher(),
                thesis.getYear(), thesis.getVolume(), thesis.getIssue(), thesis.getPages(), thesis.getDoi(), thesis.getIsbn(),
                thesis.getOnlinePublisher(), thesis.getOnlinePublishUrl(), thesis.getType(), thesis.getCopyrightStatus(),
                file, thesis.getUploader(), thesis.getApprover(), thesis.getApproveTime());
        this.delOperator = userId;
    }
}
