package com.krishiYatra.krishiYatra.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@Builder
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalFarmers;
    private long totalBuyers;
    private long totalDeliveryPartners;
    
    private long pendingVerifications;
    private long activeStocks;
    private long totalOrders;
    private long activeDemands;
    private double platformRevenue; // sum of total order amounts
    
    // For Chart.js
    private Map<String, Long> userRegistrationTrend; // Monthly registrations
    private Map<String, Long> ordersByStatus; // Pie Chart
}
