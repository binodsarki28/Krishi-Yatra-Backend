package com.krishiYatra.krishiYatra.delivery.mapper;

import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
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
        delivery.setVerified(false);
        return delivery;
    }
}
