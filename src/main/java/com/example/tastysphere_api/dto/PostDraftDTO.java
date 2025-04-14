package com.example.tastysphere_api.dto;

import lombok.Data;

@Data
public class PostDraftDTO {
    private Long id;
    private String title;
    private String content;
    private String tags;
    private Boolean isAutoSaved;
}
