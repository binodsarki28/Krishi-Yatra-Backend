package com.krishiYatra.krishiYatra.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerEntity;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByFarmer_FarmerId(String farmerId);
    List<OrderEntity> findByBuyer_BuyerId(String buyerId);
    List<OrderEntity> findByDelivery_DeliveryId(String deliveryId);

    // Farmer Stats
    long countByFarmer(FarmerEntity farmer);
    long countByFarmerAndOrderStatus(FarmerEntity farmer, OrderStatus status);
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalPrice) FROM OrderEntity o WHERE o.farmer = :farmer AND o.orderStatus = 'DELIVERED'")
    Double sumTotalPriceByFarmer(@org.springframework.data.repository.query.Param("farmer") FarmerEntity farmer);

    // Buyer Stats
    long countByBuyer(BuyerEntity buyer);
    long countByBuyerAndOrderStatus(BuyerEntity buyer, OrderStatus status);
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalPrice) FROM OrderEntity o WHERE o.buyer = :buyer AND o.orderStatus = 'DELIVERED'")
    Double sumTotalPriceByBuyer(@org.springframework.data.repository.query.Param("buyer") BuyerEntity buyer);

    // Delivery Stats
    long countByDelivery(DeliveryEntity delivery);
    long countByDeliveryAndOrderStatus(DeliveryEntity delivery, OrderStatus status);
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.deliveryFee) FROM OrderEntity o WHERE o.delivery = :delivery AND o.orderStatus = 'DELIVERED'")
    Double sumDeliveryFeeByDelivery(@org.springframework.data.repository.query.Param("delivery") DeliveryEntity delivery);

    // Chart Queries (Native for compatibility)
    @org.springframework.data.jpa.repository.Query(value = "SELECT FORMATDATETIME(o.CREATED_TIME, 'MMM') as month_label, SUM(o.TOTAL_PRICE) as total FROM ORDERS o WHERE o.FARMER_GUID = :farmerId AND o.ORDER_STATUS = 'DELIVERED' GROUP BY month_label", nativeQuery = true)
    List<Object[]> farmerRevenueTrend(@org.springframework.data.repository.query.Param("farmerId") String farmerId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT FORMATDATETIME(o.CREATED_TIME, 'MMM') as month_label, SUM(o.TOTAL_PRICE) as total FROM ORDERS o WHERE o.BUYER_GUID = :buyerId AND o.ORDER_STATUS = 'DELIVERED' GROUP BY month_label", nativeQuery = true)
    List<Object[]> buyerSpendingTrend(@org.springframework.data.repository.query.Param("buyerId") String buyerId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT FORMATDATETIME(o.CREATED_TIME, 'MMM') as month_label, SUM(o.DELIVERY_FEE) as total FROM ORDERS o WHERE o.DELIVERY_GUID = :deliveryId AND o.ORDER_STATUS = 'DELIVERED' GROUP BY month_label", nativeQuery = true)
    List<Object[]> deliveryEarningsTrend(@org.springframework.data.repository.query.Param("deliveryId") String deliveryId);

    // Admin Stats
    long countByOrderStatus(OrderStatus status);
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalPrice) FROM OrderEntity o WHERE o.orderStatus = 'DELIVERED'")
    Double sumTotalPlatformRevenue();
    
    @org.springframework.data.jpa.repository.Query(value = "SELECT ORDER_STATUS, COUNT(*) FROM ORDERS GROUP BY ORDER_STATUS", nativeQuery = true)
    List<Object[]> countOrdersByStatus();

    List<OrderEntity> findByDeliveryIsNullAndOrderStatus(OrderStatus orderStatus);
    Optional<OrderEntity> findByOrderIdAndDeliveryIsNull(String orderId);
    List<OrderEntity> findByDeliveryAndOrderStatusIn(DeliveryEntity delivery, List<OrderStatus> orderStatuses);
}
