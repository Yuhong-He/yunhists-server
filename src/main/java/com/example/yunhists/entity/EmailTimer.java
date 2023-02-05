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
@TableName("emailTimer")
public class EmailTimer {

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @TableField("email")
    private String email;

    @TableField("action")
    private String action;

    @TableField("timestamp")
    private Timestamp timestamp;

    public EmailTimer(String email, String action) {
        this.email = email;
        this.action = action;
    }

}
