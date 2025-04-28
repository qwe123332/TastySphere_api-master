package com.example.tastysphere_api.controller.admin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.UserDTO;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户（分页）
     */
    @GetMapping
    public ResponseEntity<IPage<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        IPage<User> users = userService.getAllUsers(new Page<>(page + 1, size), search);
        return ResponseEntity.ok(users);
    }


    /**
     * 获取单个用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 启用/禁用用户
     */
    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        userService.updateUserStatus(id, active);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除用户（逻辑或物理）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id); // 建议使用逻辑删除
        return ResponseEntity.ok().build();
    }
    /**
     * 修改用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser( @AuthenticationPrincipal CustomUserDetails user) {
        UserDTO userDTO = userService.getUserById(user.getUserId());
        return ResponseEntity.ok(userDTO);
    }


    @PutMapping("/users/{id}/avatar")
    public ResponseEntity<?> updateAvatar(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String avatarUrl = request.get("avatar");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("头像地址不能为空");
        }

        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setAvatar(avatarUrl);
        userService.updateByuserId(user);

        return ResponseEntity.ok("头像更新成功");
    }

}
