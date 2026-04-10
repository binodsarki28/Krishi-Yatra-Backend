package com.krishiYatra.krishiYatra.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderNotificationDto {
    private String orderId;
    private String productName;
    private String farmerUsername;
    private String buyerUsername;
    private String checkpointName;
    private String deliveryUsername;
}
