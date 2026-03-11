package com.krishiYatra.krishiYatra.buyer.dto;

import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for registering a user as a buyer")
public class RegisterBuyerRequest {

    @NotNull(message = "Consumer type is required")
    @Schema(description = "Category of the buyer (HOTEL, WHOLESALER, etc.)", example = "RETAILER")
    private ConsumerType consumerType;

    @NotBlank(message = "Business name is required")
    @Schema(description = "Registered name of the business", example = "Organic Mart")
    private String businessName;

    @NotBlank(message = "Business location is required")
    @Schema(description = "Physical address of the business", example = "Pokhara, Nepal")
    private String businessLocation;
}
