package com.krishiYatra.krishiYatra.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequest {
    @NotBlank(message = "Stock slug is required")
    private String stockSlug;

    @NotNull(message = "Order quantity is required")
    @Positive(message = "Order quantity must be greater than zero")
    private Double orderQuantity;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Drop address is required")
    private String dropAddress;


    private String checkpoints;

    @NotBlank(message = "Additional notes are required")
    private String notes;

    private Double deliveryFee;
}
