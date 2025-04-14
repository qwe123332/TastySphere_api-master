package com.example.tastysphere_api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationPreview {
    private Long userId;           // 对方用户ID
    private String username;       // 对方用户名
    private String avatar;         // 对方头像
    private String lastMessage;    // 最近一条消息内容
    private LocalDateTime lastTime; // 最近一条消息时间
    private Integer unreadCount;   // 未读消息数量
}
