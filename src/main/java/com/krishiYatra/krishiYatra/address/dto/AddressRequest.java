package com.krishiYatra.krishiYatra.address.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Ward No is required")
    @Min(value = 1, message = "Ward No must be at least 1")
    private Integer wardNo;
    private String streetName;
}
