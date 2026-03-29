package com.krishiYatra.krishiYatra.order;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.common.enums.OrderStatus;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.delivery.DeliveryEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ORDER_GUID")
    private String orderId;

    @Column(name = "ORDER_QUANTITY", nullable = false)
    private Double orderQuantity;

    @Column(name = "PER_UNIT_PRICE", nullable = false)
    private Double perUnitPrice;

    @Column(name = "TOTAL_PRICE", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "PICKUP_ADDRESS", length = 1000)
    private String pickupAddress;

    @Column(name = "DROP_ADDRESS", length = 1000)
    private String dropAddress;

    @Column(name = "DELIVERED_AT")
    private LocalDateTime deliveredAt;

    @Column(name = "CONFLICT_MESSAGE", length = 2000)
    private String conflictMessage;

    @Column(name = "CONFLICT_RAISED_AT")
    private LocalDateTime conflictRaisedAt;

    @Column(name = "CONFLICT_RESOLVED_AT")
    private LocalDateTime conflictResolvedAt;

    @Column(name = "DELIVERY_FEE")
    private Double deliveryFee;


    @Column(name = "CHECKPOINTS", length = 3000)
    private String checkpoints;

    @Column(name = "NOTES", length = 2000, nullable = false)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_GUID", referencedColumnName = "BUYER_GUID", nullable = false)
    private BuyerEntity buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FARMER_GUID", referencedColumnName = "FARMER_GUID", nullable = false)
    private FarmerEntity farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOCK_GUID", referencedColumnName = "STOCK_GUID", nullable = false)
    private StockEntity stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELIVERY_GUID", referencedColumnName = "DELIVERY_GUID")
    private DeliveryEntity delivery;
}
