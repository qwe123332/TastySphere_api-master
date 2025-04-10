package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.entity.Role;
import com.example.tastysphere_api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }
    @PostMapping
    public ResponseEntity<String> createRole(@RequestParam String name, @RequestParam String description) {
        try {
            boolean success = roleService.createRole(name, description);
            if (success) {
                return ResponseEntity.ok("✅ 角色创建成功！");
            } else {
                return ResponseEntity.ok("❌ 角色已存在！");
            }
        } catch (Exception e) {
            // 如果有异常，可以考虑返回 400 或其他状态码
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请求错误：" + e.getMessage());
        }
    }


    @PostMapping("/assign")
    public String assignPermissionsToRole(@RequestParam String roleName, @RequestParam String[] permissionNames) {
        boolean success = roleService.assignPermissionsToRole(roleName, permissionNames);
        return success ? "✅ 权限分配成功！" : "❌ 角色或权限错误！";
    }
}