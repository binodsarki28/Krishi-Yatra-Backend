package com.krishiYatra.krishiYatra.demand;

import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import com.krishiYatra.krishiYatra.common.enums.DemandStatus;
import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.stock.StockEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "DEMANDS")
@EntityListeners(AuditingEntityListener.class)
public class DemandEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DEMAND_GUID")
    private String demandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_GUID", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_CATEGORY_GUID", nullable = false)
    private SubCategoryEntity subCategory;

    @Column(name = "QUANTITY", nullable = false)
    private Double quantity;

    @Column(name = "EXPECTED_PRICE_PER_UNIT")
    private Double expectedPricePerUnit;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private DemandStatus status = DemandStatus.OPEN;

    @Column(name = "ACTIVE")
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_GUID", nullable = false)
    private BuyerEntity buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCEPTED_BY_FARMER_GUID")
    private FarmerEntity acceptedBy;

    // The stock that was created by the farmer to fulfill this demand
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FULFILLED_STOCK_GUID")
    private StockEntity fulfilledStock;
}
