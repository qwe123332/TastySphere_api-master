package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_details")
public class OrderDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId; // 替代 @ManyToOne

    @TableField("product_id")
    private Long productId; // 替代 @ManyToOne

    @TableField("quantity")
    private Integer quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("subtotal")
    private BigDecimal subtotal;

    @TableField("notes")
    private String notes;

    // 临时字段用于 DTO 映射，查询时填充
    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String productImageUrl;

    // 获取 ProductId
    public Long getProductId() {
        return productId;
    }
}
