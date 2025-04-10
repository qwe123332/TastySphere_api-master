package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.tastysphere_api.entity.Comment;
import com.example.tastysphere_api.enums.VisibilityEnum;
import com.example.tastysphere_api.handler.StringListTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@TableName("posts")
public class Post {
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long id;

    private Long userId; // 用户 ID

    private String content;
    private String title;
    @TableField(exist = false)
    private String username; // 用户名（非数据库字段）
    @TableField(exist = false)
    private String userAvatar; // 用户头像（非数据库字段）

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> images;

    private VisibilityEnum visibility;
    private Integer likeCount;
    private Integer commentCount;
    @TableField(value = "is_audited")
    private Boolean audited;
    @TableField(value = "is_approved")
    private Boolean approved;

    private String category;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private LocalDateTime auditTime;

    @TableField(exist = false)
    private List<Comment> comments; // 不存在于表中

}
