package com.krishiYatra.krishiYatra.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long pendingFarmers;
    private long pendingBuyers;
    private long pendingDelivery;
}
