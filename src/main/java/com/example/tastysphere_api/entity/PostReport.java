package com.example.tastysphere_api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post_reports")
public class PostReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;
    private Long commentId;
    private Long reporterId;
    private String reason;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String Status;
    private LocalDateTime handledAt; // 处理时间
    private Long adminId; // 处理人ID
    private LocalDateTime reviewTime; // 审核时间
}
