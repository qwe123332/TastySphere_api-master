package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_notification")
public class SystemNotification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private Boolean isGlobal;

    private Long targetUserId;

    private LocalDateTime createdAt;
}
