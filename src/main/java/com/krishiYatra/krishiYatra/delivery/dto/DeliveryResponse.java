package com.krishiYatra.krishiYatra.delivery.dto;

import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryResponse {
    private String deliveryId;
    private VehicleType vehicleType;
    private String vehicleBrand;
    private String numberPlate;
    private String licenseNumber;
    private String vehiclePhoto;
    private String licensePhoto;
    private boolean isVerified;
    private boolean isActive;
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
}
