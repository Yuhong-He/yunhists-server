package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@TableName("emailAuth")
public class EmailVerification extends Email {

    @TableField("verification_code")
    private String verificationCode;

    public EmailVerification(String email, String verificationCode) {
        super(email);
        this.verificationCode = verificationCode;
    }
}
