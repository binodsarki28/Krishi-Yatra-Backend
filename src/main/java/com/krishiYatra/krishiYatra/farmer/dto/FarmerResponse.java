package com.krishiYatra.krishiYatra.farmer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FarmerResponse {
    private String farmerId;
    private String farmName;
    private String farmLocation;
    private Double farmArea;
    private List<String> farmTypes;
    private boolean isVerified;
    private String fullName;
    private String email;
}
