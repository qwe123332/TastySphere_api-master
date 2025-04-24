package com.example.tastysphere_api.entity;

import lombok.Data;

import java.util.List;

@Data
public class DashboardStats {
    private int todayOrders;
    private double totalRevenue;
    private int activeCustomers;
    private int pendingOrders;
    private List<Integer> weeklyOrders;
}
