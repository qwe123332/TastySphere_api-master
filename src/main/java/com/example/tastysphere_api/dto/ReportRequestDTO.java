package com.example.tastysphere_api.dto;

import lombok.Data;

@Data
public class ReportRequestDTO {
    private Long postId;
    private Long commentId;
    private String reason;

    // getters & setters
}
