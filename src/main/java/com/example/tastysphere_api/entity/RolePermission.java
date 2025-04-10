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
@TableName("role_permissions")
public class RolePermission {

    @TableField("role_id")
    private Long roleId; // 替代 @ManyToOne (Role)

    @TableField("permission_id")
    private Long permissionId; // 替代 @ManyToOne (Permission)
}
