package com.example.tastysphere_api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.dto.request.NotificationRequest;
import com.example.tastysphere_api.dto.request.ReportReviewRequest;
import com.example.tastysphere_api.entity.*;

import com.example.tastysphere_api.service.AdminService;
import com.example.tastysphere_api.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final NotificationService notificationService;
    private final AdminService adminService;
    @Autowired
    public AdminController(NotificationService notificationService, AdminService adminService) {
        this.notificationService = notificationService;
        this.adminService = adminService;

    }



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






    @PostMapping("/users/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        adminService.updateUserStatus(userId, active);
        return ResponseEntity.ok().build();
    }





    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/{id}")
    public ResponseEntity<PostReport> getReportDetail(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getReportById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reports/{id}/review")
    public ResponseEntity<Void> reviewReport(
            @PathVariable Long id,
            @RequestBody ReportReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails admin) {
        adminService.reviewReport(id, request, admin.getUser().getId());
        return ResponseEntity.ok().build();
    }





    @GetMapping("/audit-logs")
    public ResponseEntity<IPage<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAuditLogs(page, size));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IPage<PostReport>> getReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getReports(page, size, status));
    }


}