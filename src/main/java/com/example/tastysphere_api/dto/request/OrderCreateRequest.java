package com.example.tastysphere_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long merchantId;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private String deliveryAddress;

    @NotNull
    private String contactPhone;

    private List<OrderItemRequest> orderItems;
}