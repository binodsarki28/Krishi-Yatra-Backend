package com.krishiYatra.krishiYatra.buyer.dto;

import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerResponse {
    private String buyerId;
    private ConsumerType consumerType;
    private String businessName;
    private String businessLocation;
    private boolean isVerified;
    private boolean isActive;
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
}
