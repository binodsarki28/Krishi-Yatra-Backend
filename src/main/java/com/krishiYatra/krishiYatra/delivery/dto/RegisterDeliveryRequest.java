package com.krishiYatra.krishiYatra.delivery.dto;

import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for registering a user as a delivery partner")
public class RegisterDeliveryRequest {

    @NotNull(message = "Vehicle type is required")
    @Schema(description = "Type of vehicle used for delivery", example = "VAN")
    private VehicleType vehicleType;

    @NotBlank(message = "Vehicle brand is required")
    @Schema(description = "Brand and model of the vehicle", example = "Tata Intra V30")
    private String vehicleBrand;

    @NotBlank(message = "Number plate is required")
    @Schema(description = "Registration number plate", example = "BA 1 PA 1234")
    private String numberPlate;

    @NotBlank(message = "License number is required")
    @Schema(description = "Driving license number", example = "01-01-00000000")
    private String licenseNumber;

    @Schema(description = "URL/Path to vehicle photo")
    private String vehiclePhoto;

    @Schema(description = "URL/Path to license photo")
    private String licensePhoto;
}
