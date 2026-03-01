package com.krishiYatra.krishiYatra.delivery.mapper;

import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryListResponse;
import com.krishiYatra.krishiYatra.delivery.dto.DeliveryDetailResponse;
import com.krishiYatra.krishiYatra.delivery.dto.RegisterDeliveryRequest;

public interface DeliveryMapper {
    DeliveryEntity toEntity(RegisterDeliveryRequest request);
    DeliveryListResponse toResponse(DeliveryEntity delivery);
    DeliveryDetailResponse toDetailResponse(DeliveryEntity delivery);
}
