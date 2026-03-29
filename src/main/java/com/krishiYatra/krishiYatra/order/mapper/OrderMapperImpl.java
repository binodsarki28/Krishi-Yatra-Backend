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
//        entity.setVehicleType(request.getVehicleType());
        entity.setCheckpoints(request.getCheckpoints());
        entity.setNotes(request.getNotes());
        entity.setDeliveryFee(request.getDeliveryFee());
        return entity;
    }

    @Override
    public com.krishiYatra.krishiYatra.order.dto.OrderResponse toResponse(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        com.krishiYatra.krishiYatra.order.dto.OrderResponse response = new com.krishiYatra.krishiYatra.order.dto.OrderResponse();
        response.setOrderId(entity.getOrderId());
        StockEntity stock = entity.getStock();
        if (stock != null) {
            response.setStockSlug(stock.getStockSlug());
            response.setProductName(stock.getProductName());
        }
        response.setOrderQuantity(entity.getOrderQuantity());
        response.setPerUnitPrice(entity.getPerUnitPrice());
        response.setTotalPrice(entity.getTotalPrice());
        response.setOrderStatus(entity.getOrderStatus());
        response.setPickupAddress(entity.getPickupAddress());
        response.setDropAddress(entity.getDropAddress());
        response.setDeliveryFee(entity.getDeliveryFee());
//        response.setVehicleType(entity.getVehicleType());
        response.setCheckpoints(entity.getCheckpoints());
        response.setNotes(entity.getNotes());
        response.setCreatedAt(entity.getCreatedAt());
        response.setConflictMessage(entity.getConflictMessage());

        if (entity.getFarmer() != null && entity.getFarmer().getUser() != null) {
            response.setFarmerName(entity.getFarmer().getUser().getFullName());
            response.setFarmerPhone(entity.getFarmer().getUser().getPhoneNumber());
        }

        if (entity.getBuyer() != null && entity.getBuyer().getUser() != null) {
            response.setBuyerName(entity.getBuyer().getUser().getFullName());
            response.setBuyerPhone(entity.getBuyer().getUser().getPhoneNumber());
        }

        if (entity.getDelivery() != null && entity.getDelivery().getUser() != null) {
            response.setDeliveryName(entity.getDelivery().getUser().getFullName());
            response.setDeliveryPhone(entity.getDelivery().getUser().getPhoneNumber());
        }

        return response;
    }
}
