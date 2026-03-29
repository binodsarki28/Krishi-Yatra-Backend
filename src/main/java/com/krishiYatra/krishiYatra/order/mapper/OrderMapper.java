package com.krishiYatra.krishiYatra.order.mapper;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.order.OrderEntity;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import com.krishiYatra.krishiYatra.order.dto.OrderResponse;
public interface OrderMapper {
    OrderEntity toEntity(OrderCreateRequest request, BuyerEntity buyer, StockEntity stock);
    OrderResponse toResponse(OrderEntity entity);
}
