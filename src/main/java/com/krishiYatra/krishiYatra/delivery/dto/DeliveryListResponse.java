package com.krishiYatra.krishiYatra.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryListResponse {
    private String fullName;
    private String username;
    private VehicleType vehicleType;
    private String vehicleBrand;
    
    @JsonProperty("status")
    private VerificationStatus status;
    
    @JsonProperty("isActive")
    private boolean isActive;
}
