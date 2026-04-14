package com.krishiYatra.krishiYatra.order.mapper;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.order.OrderEntity;
import com.krishiYatra.krishiYatra.order.dto.OrderCreateRequest;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        BigDecimal pricePerUnit = BigDecimal.valueOf(stock.getPricePerUnit());
        BigDecimal quantity = BigDecimal.valueOf(request.getOrderQuantity());

        BigDecimal subTotal = pricePerUnit.multiply(quantity);

        BigDecimal deliveryFee = request.getDeliveryFee() != null
                ? BigDecimal.valueOf(request.getDeliveryFee())
                : BigDecimal.ZERO;

        BigDecimal total = subTotal.add(deliveryFee);

        deliveryFee = deliveryFee.setScale(2, RoundingMode.HALF_UP);
        total = total.setScale(2, RoundingMode.HALF_UP);

        entity.setPerUnitPrice(pricePerUnit.doubleValue());
        entity.setDeliveryFee(deliveryFee.doubleValue());
        entity.setTotalPrice(total.doubleValue());

        entity.setPickupAddress(request.getPickupAddress());
        entity.setDropAddress(request.getDropAddress());
        entity.setCheckpoints(request.getCheckpoints());
        entity.setNotes(request.getNotes());

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
        response.setCheckpoints(entity.getCheckpoints());
        response.setNotes(entity.getNotes());
        response.setCreatedAt(entity.getCreatedAt());
        response.setConflictMessage(entity.getConflictMessage());
        response.setConflictRaisedAt(entity.getConflictRaisedAt());

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
