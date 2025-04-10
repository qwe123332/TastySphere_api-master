package com.example.tastysphere_api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String content;
    private String type;
    private Long relatedId;
    private boolean read;
    private LocalDateTime createdTime;
} 