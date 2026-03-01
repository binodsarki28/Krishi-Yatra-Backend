package com.krishiYatra.krishiYatra.buyer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyerListResponse {
    private String fullName;
    private String username;
    private ConsumerType consumerType;
    private String businessLocation;
    
    @JsonProperty("isVerified")
    private boolean isVerified;
    
    @JsonProperty("isActive")
    private boolean isActive;
}
