package com.krishiYatra.krishiYatra.farmer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class FarmerDashboardResponse {
    private long totalStocks;
    private long activeStocks;
    private long outOfStock;
    
    private long totalOrders;
    private long completedOrders;
    private long pendingOrders;
    
    private double totalRevenue;
    
    // For Chart.js
    private Map<String, Long> stocksByCategory; // Pie Chart
    private Map<String, Double> revenueByMonth; // Line Chart
    
    private List<Map<String, Object>> recentOrders;
}
