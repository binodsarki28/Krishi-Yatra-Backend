package com.krishiYatra.krishiYatra.address.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressResponse {
    private String province;
    private String district;
    private String municipality;
    private int wardNo;
    private String streetName;
    private String fullAddress;
}
