package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("likes")
public class Like {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId; // 存储 User ID（替代 @ManyToOne）

    @TableField("post_id")
    private Long postId; // 存储 Post ID

    @TableField("comment_id")
    private Long commentId; // 存储 Comment ID

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    // 临时字段用于 DTO 映射，查询时填充
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String postTitle;

    @TableField(exist = false)
    private String commentContent;
}
