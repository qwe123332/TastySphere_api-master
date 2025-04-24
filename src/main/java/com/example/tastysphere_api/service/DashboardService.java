package com.example.tastysphere_api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.tastysphere_api.entity.DashboardStats;
import com.example.tastysphere_api.entity.Post;
import com.example.tastysphere_api.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final PostReportMapper reportMapper;

private final DashboardMapper dashboardMapper;


    public DashboardService(UserMapper userMapper, PostMapper postMapper, PostReportMapper reportMapper,  DashboardMapper dashboardMapper) {
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.reportMapper = reportMapper;
        this.dashboardMapper = dashboardMapper;

    }
    public DashboardStats getMerchantStats(Long merchantId) {
        DashboardStats stats = new DashboardStats();
        stats.setTodayOrders(dashboardMapper.countTodayOrders(merchantId));
        stats.setTotalRevenue(dashboardMapper.sumTotalRevenue(merchantId));
        stats.setActiveCustomers(dashboardMapper.countActiveCustomers(merchantId));
        stats.setPendingOrders(dashboardMapper.countPendingOrders(merchantId));
        stats.setWeeklyOrders(buildWeeklyOrderList(merchantId));
        return stats;
    }
    public List<Integer> buildWeeklyOrderList(Long merchantId) {
        List<Map<String, Object>> raw = dashboardMapper.getWeeklyOrders(merchantId);
        Map<String, Integer> dateToCount = raw.stream().collect(Collectors.toMap(
                m -> m.get("date").toString(),
                m -> ((Number) m.get("count")).intValue()
        ));

        List<Integer> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 6; i >= 0; i--) {
            String day = LocalDate.now().minusDays(i).format(fmt);
            result.add(dateToCount.getOrDefault(day, 0));
        }
        return result;
    }

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> map = new HashMap<>();

        // 基础统计
        map.put("totalUsers", userMapper.selectCount(null));
        map.put("totalPosts", postMapper.selectCount(null));
        map.put("pendingPosts", postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getAudited, false)
        ));
        map.put("reportCount", reportMapper.selectCount(null));

        // 上次登录可从 token/session 解析，这里示例写死
        map.put("lastLogin", "2025-04-01 12:00");

        // 模拟系统状态
        Map<String, Integer> systemHealth = new HashMap<>();
        systemHealth.put("cpu", (int) (Math.random() * 60 + 30));
        systemHealth.put("memory", (int) (Math.random() * 30 + 60));
        map.put("systemHealth", systemHealth);

        return map;
    }
}
