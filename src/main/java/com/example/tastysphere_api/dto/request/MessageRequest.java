package com.example.tastysphere_api.dto.request;

import lombok.Data;

@Data
public class MessageRequest {
    private Long receiverId;
    private String content;
    private String messageType;
    // getter/setter
}
