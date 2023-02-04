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
@TableName("emailAuth")
public class EmailVerification {

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @TableField("email")
    private String email;

    @TableField("verification_code")
    private String verificationCode;

    @TableField("timestamp")
    private Timestamp timestamp;

    public EmailVerification(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
    }
}
