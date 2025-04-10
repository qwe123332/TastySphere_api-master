package com.example.tastysphere_api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("roles")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name; // 角色名 (e.g. "ADMIN", "USER", "MERCHANT")

    @TableField("description")
    private String description;

    // 由于多对多关系，直接映射 Permission 会导致复杂化，因此我们用临时字段处理
    @TableField(exist = false)
    private Set<Permission> permissions = new HashSet<>();

    // 增加权限
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
}
