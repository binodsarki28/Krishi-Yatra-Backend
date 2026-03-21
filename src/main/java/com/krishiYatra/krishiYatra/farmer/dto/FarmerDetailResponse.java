package com.krishiYatra.krishiYatra.farmer.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;

@Getter
@Setter
public class FarmerDetailResponse {
    private String farmerId;
    private String farmName;
    private String farmLocation;
    private Double farmArea;
    private List<String> farmTypes;
    private VerificationStatus status;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String username;
    private boolean active;
    private LocalDateTime createdAt;
}
