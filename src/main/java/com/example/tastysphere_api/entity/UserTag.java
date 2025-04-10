package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("user_tags")
public class UserTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId; // 替代 @ManyToOne

    @TableField("tag_name")
    private String tagName;

    @TableField("tag_type")
    private String tagType; // CUISINE, TASTE, DIET_PREFERENCE

    @TableField("weight")
    private Double weight; // 标签权重
}
