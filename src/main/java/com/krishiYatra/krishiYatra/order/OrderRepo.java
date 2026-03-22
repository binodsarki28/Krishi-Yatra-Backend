package com.krishiYatra.krishiYatra.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByBuyer_BuyerId(String buyerId);
    List<OrderEntity> findByDeliveryIsNullAndOrderStatus(OrderStatus orderStatus);
    List<OrderEntity> findByDeliveryIsNullAndOrderStatusAndVehicleType(OrderStatus orderStatus, String vehicleType);
    Optional<OrderEntity> findByOrderIdAndDeliveryIsNull(String orderId);
    List<OrderEntity> findByDelivery_DeliveryId(String deliveryId);
    List<OrderEntity> findByDeliveryAndOrderStatusIn(DeliveryEntity delivery, List<OrderStatus> orderStatuses);
}
