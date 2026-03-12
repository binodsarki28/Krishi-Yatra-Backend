package com.krishiYatra.krishiYatra.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryListResponse {
    private String fullName;
    private String username;
    private VehicleType vehicleType;
    private String vehicleBrand;
    
    @JsonProperty("isVerified")
    private boolean isVerified;
    
    @JsonProperty("isActive")
    private boolean isActive;
}
