package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Email {

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @TableField("email")
    private String email;

    @TableField("timestamp")
    private Timestamp timestamp;

    public Email(String email) {
        this.email = email;
    }

}
