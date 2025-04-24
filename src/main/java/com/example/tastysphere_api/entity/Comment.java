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
@TableName("comments")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("user_id")
    private Long userId;

    @TableField("content")
    private String content;

    @TableField("parent_id")
    private Long parentCommentId;

    @TableField("like_count")
    private Integer likeCount = 0;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    // 若用于 DTO 映射可保留临时字段，但需标注 exist = false
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String userAvatar;

    @TableField(exist = false)
    private String postTitle;

    @TableField(exist = false)
    private String parentContent;



}
