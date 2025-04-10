package com.example.tastysphere_api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;
}