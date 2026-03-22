package com.krishiYatra.krishiYatra.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {
    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Municipality is required")
    private String municipality;

    private String city;

    private String wardNo;
    private String streetName;

    @NotBlank(message = "Specific location details are required")
    private String other;
}
