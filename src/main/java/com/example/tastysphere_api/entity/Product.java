package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("products")
public class Product {

    @TableId(value = "product_id", type = IdType.AUTO)
    private Long productId;


    @TableField("user_id")
    private Long userId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("price")
    private BigDecimal price;

    @TableField("category")
    private String category;

    @TableField("image_url")
    private String imageUrl;

    @TableField("status")
    private Status status = Status.ACTIVE;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    public enum Status {
        ACTIVE, INACTIVE, OUT_OF_STOCK
    }
}
