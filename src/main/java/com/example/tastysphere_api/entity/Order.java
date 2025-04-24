package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long orderId;

    @TableField("user_id")
    private Long userId; // 替代 @ManyToOne

    @TableField("merchant_id")
    private Long merchantId; // 替代 @ManyToOne

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    private Status status = Status.PENDING;

    @TableField("payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @TableField("payment_method")
    private PaymentMethod paymentMethod = PaymentMethod.ONLINE;

    @TableField("delivery_status")
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @TableField("delivery_address")
    private String deliveryAddress;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public enum Status {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    public enum PaymentStatus {
        UNPAID, PAID, REFUNDED
    }

    public enum PaymentMethod {
        CASH, CARD, ONLINE
    }

    public enum DeliveryStatus {
        PENDING, IN_PROGRESS, DELIVERED
    }
}
