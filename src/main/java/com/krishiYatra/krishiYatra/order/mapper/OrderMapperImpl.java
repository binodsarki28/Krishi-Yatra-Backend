package com.krishiYatra.krishiYatra.order.mapper;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.order.OrderEntity;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderEntity toEntity(OrderCreateRequest request, BuyerEntity buyer, StockEntity stock) {
        if (request == null || buyer == null || stock == null) {
            return null;
        }

        OrderEntity entity = new OrderEntity();
        entity.setBuyer(buyer);
        entity.setFarmer(stock.getFarmer());
        entity.setStock(stock);
        entity.setOrderQuantity(request.getOrderQuantity());
        entity.setPerUnitPrice(stock.getPricePerUnit());
        entity.setTotalPrice(stock.getPricePerUnit() * request.getOrderQuantity());
        entity.setPickupAddress(request.getPickupAddress());
        entity.setDropAddress(request.getDropAddress());
        entity.setVehicleType(request.getVehicleType());
        entity.setCheckpoints(request.getCheckpoints());
        entity.setNotes(request.getNotes());
        entity.setDeliveryFee(request.getDeliveryFee());
        return entity;
    }
}
