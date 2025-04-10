package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_behaviors")
public class UserBehavior {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("behavior_type")
    private String behaviorType;

    @TableField("target_id")
    private Long targetId;

    @TableField("target_type")
    private String targetType;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("weight")
    private Double weight;
}
