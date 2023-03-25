package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@TableName("emailTimer")
public class EmailTimer extends Email {

    @TableField("action")
    private String action;

    public EmailTimer(String email, String action) {
        super(email);
        this.action = action;
    }

}
