package com.krishiYatra.krishiYatra.farmer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for admin to verify or reject a farmer")
public class VerifyFarmerRequest {
    
    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Status (Approved: true, Rejected: false) is required")
    private Boolean approved;

    @Schema(description = "Reason for rejection (required only if approved is false)", example = "Invalid farm documents")
    private String reason;
}
