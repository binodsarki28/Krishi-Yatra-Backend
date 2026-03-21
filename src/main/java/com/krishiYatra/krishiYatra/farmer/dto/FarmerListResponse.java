package com.krishiYatra.krishiYatra.farmer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FarmerListResponse {
    private String fullName;
    private String username;
    private String farmTypes;
    private String farmLocation;
    
    @JsonProperty("status")
    private VerificationStatus status;
    
    @JsonProperty("isActive")
    private boolean isActive;
}
