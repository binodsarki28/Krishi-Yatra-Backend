package com.krishiYatra.krishiYatra.buyer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyerListResponse {
    private String fullName;
    private String username;
    private ConsumerType consumerType;
    private String businessLocation;
    
    @JsonProperty("status")
    private VerificationStatus status;
    
    @JsonProperty("isActive")
    private boolean isActive;
}
