package com.krishiYatra.krishiYatra.delivery.mapper;

import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;

public interface DeliveryMapper {
    DeliveryEntity toEntity(RegisterDeliveryRequest request);
}
