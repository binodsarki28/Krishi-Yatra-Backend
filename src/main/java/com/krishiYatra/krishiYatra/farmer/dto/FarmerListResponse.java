package com.krishiYatra.krishiYatra.farmer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FarmerListResponse {
    private String fullName;
    private String username;
    private String farmTypes;
    private String farmLocation;
    
    @JsonProperty("isVerified")
    private boolean isVerified;
    
    @JsonProperty("isActive")
    private boolean isActive;
}
