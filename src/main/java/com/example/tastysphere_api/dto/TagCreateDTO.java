package com.example.tastysphere_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagCreateDTO {
    @NotBlank(message = "标签名称不能为空")
    private String name;
}
