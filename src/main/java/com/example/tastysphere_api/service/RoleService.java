package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tastysphere_api.entity.Permission;
import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.entity.RolePermission;
import com.example.tastysphere_api.mapper.PermissionMapper;
import com.example.tastysphere_api.mapper.RoleMapper;
import com.example.tastysphere_api.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    public boolean assignPermissionsToRole(String roleName, String[] permissionNames) {
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("name", roleName));
        if (role == null) {
            System.err.println("❌ 角色 " + roleName + " 不存在！");
            return false;
        }

        List<Permission> permissions = permissionMapper.selectList(
                new QueryWrapper<Permission>().in("name", Arrays.asList(permissionNames)));

        if (permissions.isEmpty()) {
            System.err.println("⚠️ 未找到任何匹配的权限！");
            return false;
        }

        // 删除旧关系
        rolePermissionMapper.delete(new QueryWrapper<RolePermission>().eq("role_id", role.getId()));

        // 插入新关系（注意使用 Long 类型）
        List<RolePermission> rolePermissions = permissions.stream()
                .map(p -> new RolePermission(role.getId(), p.getId())) // ✅ 修正这里
                .collect(Collectors.toList());

        for (RolePermission rp : rolePermissions) {
            rolePermissionMapper.insert(rp);
        }

        System.out.println("✅ 成功为角色 " + roleName + " 分配权限：" +
                permissions.stream().map(Permission::getName).collect(Collectors.joining(", ")));
        return true;
    }

    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }

    public boolean createRole(String name, String description) {
        try {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            return roleMapper.insert(role) > 0;
        } catch (Exception e) {
            System.err.println("❌ 创建角色时发生错误: " + e.getMessage());
            return false;
        }
    }
}
