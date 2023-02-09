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
@TableName("category")
public class Category {

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @TableField("zh_name")
    private String zhName;

    @TableField("en_name")
    private String enName;

    @TableField("cat_theses")
    private int catTheses;

    @TableField("cat_subcats")
    private int catSubCats;

    @TableField("operator")
    private int operator;

    @TableField("created_at")
    private Timestamp created_at;

    public Category(String zhName, String enName, int operator) {
        this.zhName = zhName;
        this.enName = enName;
        this.operator = operator;
        this.catTheses = 0;
        this.catSubCats = 0;
    }

}
