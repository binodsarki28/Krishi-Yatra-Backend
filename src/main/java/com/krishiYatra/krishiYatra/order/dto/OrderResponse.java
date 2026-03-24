package com.krishiYatra.krishiYatra.order.dto;

import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderResponse {
    private String orderId;
    private String stockSlug;
    private String productName;
    private Double orderQuantity;
    private Double perUnitPrice;
    private Double totalPrice;
    private OrderStatus orderStatus;
    private String pickupAddress;
    private String dropAddress;
    private Double deliveryFee;
    private String vehicleType;
    private String checkpoints;
    private String notes;
    
    // Additional Participant Fields
    private String farmerName;
    private String farmerPhone;
    private String buyerName;
    private String buyerPhone;
    private String deliveryName;
    private String deliveryPhone;

    private LocalDateTime createdAt;
}
