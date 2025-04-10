package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.entity.AuditLog;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.entity.User;
import com.example.tastysphere_api.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(adminService.getSystemStatistics());
    }

    @GetMapping("/statistics/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedStatistics() {
        return ResponseEntity.ok(adminService.getDetailedStatistics());
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        return ResponseEntity.ok(adminService.getSystemMetrics());
    }

    @GetMapping("/posts/pending")
    public ResponseEntity<Page<Post>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getPendingPosts(PageRequest.of(page, size)));
    }

    @PostMapping("/posts/{postId}/audit")
    public ResponseEntity<Void> auditPost(
            @PathVariable Long postId,
            @RequestParam boolean approved,
            @RequestParam String reason,
            @AuthenticationPrincipal CustomUserDetails admin) {
        adminService.auditPost(postId, approved, admin.getUser(), reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getUsers(page, size));
    }

    @PostMapping("/users/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        adminService.updateUserStatus(userId, active);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<org.springframework.data.domain.Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Assuming adminService.getAuditLogs expects a MyBatis-Plus Page or some custom Page
        // Replace with the appropriate import and constructor for your custom Page class
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AuditLog> mybatisPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);

        // Call service with MyBatis-Plus Page
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AuditLog> resultPage =
                adminService.getAuditLogs(mybatisPage);

        // Convert to Spring Data Page
        PageImpl<AuditLog> springPage = new PageImpl<>(
                resultPage.getRecords(),
                PageRequest.of(page, size),
                resultPage.getTotal()
        );

        return ResponseEntity.ok(springPage);
    }
}