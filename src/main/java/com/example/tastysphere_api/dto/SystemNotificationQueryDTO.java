package com.example.tastysphere_api.dto;

import lombok.Data;

@Data
public class SystemNotificationQueryDTO {
    private String type;
    private Boolean isGlobal;
    private Boolean isRead;
    private String title;
    private String content;
    private Long userId;
    private String startDate;
    private String endDate;
    private Integer page = 1;
    private Integer size = 10;
}
