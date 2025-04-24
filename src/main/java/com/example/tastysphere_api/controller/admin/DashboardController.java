package com.example.tastysphere_api.controller.admin;

import com.example.tastysphere_api.service.DashboardService;
import com.example.tastysphere_api.service.SystemHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SystemHealthService systemHealthService;

    public DashboardController(DashboardService dashboardService, SystemHealthService systemHealthService) {
        this.dashboardService = dashboardService;
        this.systemHealthService = systemHealthService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> result = dashboardService.getDashboardStatistics();
        return ResponseEntity.ok(result);
    }
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {

        return ResponseEntity.ok(systemHealthService.getHealthStatus());
    }

}
