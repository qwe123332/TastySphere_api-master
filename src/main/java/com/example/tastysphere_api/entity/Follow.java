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
@TableName("follows")
public class Follow {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("follower_id")
    private Long followerId; // 替代原 @ManyToOne

    @TableField("following_id")
    private Long followingId; // 替代原 @ManyToOne

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    // 临时字段用于 DTO 映射，查询时填充
    @TableField(exist = false)
    private String followerUsername;

    @TableField(exist = false)
    private String followingUsername;
}
