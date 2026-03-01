package com.krishiYatra.krishiYatra.delivery.dto;

import com.krishiYatra.krishiYatra.common.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class DeliveryDetailResponse {
    private String deliveryId;
    private VehicleType vehicleType;
    private String vehicleBrand;
    private String numberPlate;
    private String licenseNumber;
    private String vehiclePhoto;
    private String licensePhoto;
    private boolean verified;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String username;
    private boolean active;
    private LocalDateTime createdAt;
}
