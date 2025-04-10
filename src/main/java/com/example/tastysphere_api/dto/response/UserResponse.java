package com.example.tastysphere_api.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String avatar;
}