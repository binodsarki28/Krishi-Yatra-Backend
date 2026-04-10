package com.krishiYatra.krishiYatra.delivery.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@Builder
public class DeliveryDashboardResponse {
    private long totalDeliveries;
    private long pendingDeliveries;
    private long completedDeliveries;
    private double totalEarnings;
    
    // For Chart.js
    private Map<String, Long> deliveryStatusDistribution;
    private Map<String, Double> earningsTrend;
}
