package com.example.tastysphere_api.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    private String status;
    private String deliveryStatus;
    private String deliveryAddress;
    private String contactPhone;
    private List<OrderItemResponse> orderItems;
}