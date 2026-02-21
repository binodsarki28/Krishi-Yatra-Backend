package com.krishiYatra.krishiYatra.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for admin to verify or reject a delivery partner")
public class VerifyDeliveryRequest {
    
    @NotNull(message = "Delivery partner registration ID is required")
    private String deliveryId;

    @NotNull(message = "Status (Approved: true, Rejected: false) is required")
    private Boolean approved;

    @Schema(description = "Reason for rejection (required only if approved is false)", example = "License expired")
    private String reason;
}
