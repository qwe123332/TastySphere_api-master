package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_reviews")
public class MerchantReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("merchant_id")
    private Long merchantId; // 替代原 @ManyToOne

    @TableField("user_id")
    private Long userId; // 替代原 @ManyToOne

    @TableField("rating")
    private Integer rating; // 评分 1-5

    @TableField("content")
    private String content;

    @TableField("images")
    private String images; // JSON 数组存储图片 URL

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
