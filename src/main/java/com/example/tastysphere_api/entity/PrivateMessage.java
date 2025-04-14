package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@TableName("private_message")
@Data
public class PrivateMessage {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;

    @TableField("is_read")
    private Boolean read;

    private LocalDateTime timestamp;
}
