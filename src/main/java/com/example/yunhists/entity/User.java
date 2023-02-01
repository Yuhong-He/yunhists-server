package com.example.yunhists.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("user")
public class User {

    @TableId(value="id", type=IdType.AUTO)
    private Integer id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;

    @TableField("user_rights")
    private Integer userRights;

    @TableField("lang")
    private Integer lang;

    @TableField("points")
    private Integer points;

    @TableField("register_type")
    private Integer registerType;

    public User(String username, String password, String email, int registerType) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.registerType = registerType;
    }
}
