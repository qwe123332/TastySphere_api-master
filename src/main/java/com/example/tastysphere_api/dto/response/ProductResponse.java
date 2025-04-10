package com.example.tastysphere_api.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long productId;
    private Long merchantId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
}