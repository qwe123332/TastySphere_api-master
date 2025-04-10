package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("permissions")
public class Permission {

    @TableId(value = "permission_id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name; // 权限标识符

    @TableField("description")
    private String description;
}
