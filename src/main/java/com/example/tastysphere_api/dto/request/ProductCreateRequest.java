package com.example.tastysphere_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {
    @NotNull
    private Long merchantId;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    private String category;
    private String imageUrl;
}