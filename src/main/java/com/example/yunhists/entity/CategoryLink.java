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
@TableName("categorylinks")
public class CategoryLink {

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    @TableField("cat_from")
    private int catFrom;

    @TableField("cat_to")
    private int catTo;

    @TableField("cat_to_zhName")
    private String catToZhName;

    @TableField("cat_to_enName")
    private String catToEnName;

    @TableField("cat_type")
    private int catType;

    @TableField("operator")
    private int operator;

    @TableField("created_at")
    private Timestamp createdAt;

    public CategoryLink(int catFrom, int catTo, String catToZhName, String catToEnName, int catType, int operator) {
        this.catFrom = catFrom;
        this.catTo = catTo;
        this.catToZhName = catToZhName;
        this.catToEnName = catToEnName;
        this.catType = catType;
        this.operator = operator;
    }

}
