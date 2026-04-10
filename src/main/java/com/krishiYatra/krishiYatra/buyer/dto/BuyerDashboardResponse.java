package com.krishiYatra.krishiYatra.buyer.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@Builder
public class BuyerDashboardResponse {
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private double totalSpent;
    
    // For Chart.js
    private Map<String, Long> ordersByCategory;
    private Map<String, Double> spendingTrend; // Monthly spending
}
