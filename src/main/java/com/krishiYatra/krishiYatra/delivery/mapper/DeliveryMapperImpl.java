package com.krishiYatra.krishiYatra.delivery.mapper;

import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryListResponse;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryDetailResponse;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;
import org.springframework.stereotype.Service;

@Service
public class DeliveryMapperImpl implements DeliveryMapper {

    @Override
    public DeliveryEntity toEntity(RegisterDeliveryRequest request) {
        if (request == null) {
            return null;
        }
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setVehicleType(request.getVehicleType());
        delivery.setVehicleBrand(request.getVehicleBrand());
        delivery.setNumberPlate(request.getNumberPlate());
        delivery.setLicenseNumber(request.getLicenseNumber());
        delivery.setVehiclePhoto(request.getVehiclePhoto());
        delivery.setLicensePhoto(request.getLicensePhoto());
        delivery.setStatus(com.krishiYatra.krishiYatra.common.enums.VerificationStatus.PENDING);
        return delivery;
    }

    @Override
    public DeliveryListResponse toResponse(DeliveryEntity delivery) {
        if (delivery == null) {
            return null;
        }
        DeliveryListResponse response = new DeliveryListResponse();
        response.setStatus(delivery.getStatus());

        if (delivery.getUser() != null) {
            response.setFullName(delivery.getUser().getFullName());
            response.setUsername(delivery.getUser().getUsername());
            response.setActive(delivery.getUser().isActive());
        }

        return response;
    }

    @Override
    public DeliveryDetailResponse toDetailResponse(DeliveryEntity delivery) {
        if (delivery == null) {
            return null;
        }
        DeliveryDetailResponse response = new DeliveryDetailResponse();
        response.setDeliveryId(delivery.getDeliveryId());
        response.setVehicleType(delivery.getVehicleType());
        response.setVehicleBrand(delivery.getVehicleBrand());
        response.setNumberPlate(delivery.getNumberPlate());
        response.setLicenseNumber(delivery.getLicenseNumber());
        response.setVehiclePhoto(delivery.getVehiclePhoto());
        response.setLicensePhoto(delivery.getLicensePhoto());
        response.setStatus(delivery.getStatus());
        response.setCreatedAt(delivery.getCreatedAt());

        if (delivery.getUser() != null) {
            response.setFullName(delivery.getUser().getFullName());
            response.setUsername(delivery.getUser().getUsername());
            response.setEmail(delivery.getUser().getEmail());
            response.setPhoneNumber(delivery.getUser().getPhoneNumber());
            response.setActive(delivery.getUser().isActive());
        }

        return response;
    }
}
