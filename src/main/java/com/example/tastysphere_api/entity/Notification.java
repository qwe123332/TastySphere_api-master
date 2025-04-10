package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notifications")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId; // 替代 @ManyToOne

    @TableField("content")
    private String content;

    @TableField("type")
    private String type; // COMMENT, LIKE, FOLLOW

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt = LocalDateTime.now();

    @TableField("is_read")
    private boolean isRead = false;

    @TableField("related_id")
    private Long relatedId; // 相关的评论ID、点赞ID等
}
