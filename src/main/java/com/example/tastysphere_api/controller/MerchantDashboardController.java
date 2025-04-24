package com.example.tastysphere_api.controller;

import com.example.tastysphere_api.dto.CustomUserDetails;
import com.example.tastysphere_api.entity.DashboardStats;
import com.example.tastysphere_api.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class MerchantDashboardController {
    private final DashboardService dashboardService;
    public MerchantDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/merchant")
    public DashboardStats getMerchantDashboard(@AuthenticationPrincipal CustomUserDetails user) {
        Long merchantId = user.getUser().getId();
        DashboardStats merchantStats = dashboardService.getMerchantStats(merchantId);
        return merchantStats;
    }
}
