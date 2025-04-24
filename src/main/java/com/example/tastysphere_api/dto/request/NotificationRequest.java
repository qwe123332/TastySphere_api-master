package com.example.tastysphere_api.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class NotificationRequest {
    private List<Long> userIds; // 可为 null 表示全部
    private String content;
    private String type; // 系统公告、违规通知等
    private LocalDateTime time; // 可为 null 表示立即发送


}
