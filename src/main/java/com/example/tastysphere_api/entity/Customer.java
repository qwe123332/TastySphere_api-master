package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("customers")
public class Customer {
    @TableId
    private Long id;
    private String name;
    private String phone;
    private String level;
    private LocalDate registeredAt;
}
