package com.example.tastysphere_api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private String fromUserId;
    private String toUserId;
    private String content;
    private LocalDateTime createdTime;
}
