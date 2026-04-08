package com.krishiYatra.krishiYatra.buyer.dto;

import com.krishiYatra.krishiYatra.common.enums.ConsumerType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BuyerDetailResponse {
    private String buyerId;
    private ConsumerType consumerType;
    private String businessName;
    private String businessLocation;
    private com.krishiYatra.krishiYatra.common.enums.VerificationStatus status;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String username;
    private boolean active;
    private LocalDateTime createdAt;
}
