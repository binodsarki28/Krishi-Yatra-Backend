package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.db.Auditable;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "STOCKS")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class StockEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "STOCK_GUID")
    private String stockId;

    @Column(name = "STOCK_NAME")
    private String stockName;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "STOCK_SLUG", unique = true)
    private String stockSlug;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STOCK_IMAGES", columnDefinition = "TEXT")
    private String stockImages;

    @Column(name = "QUANTITY")
    private Double quantity;

    @Column(name = "PRICE_PER_UNIT")
    private Double pricePerUnit;

    @Column(name = "ACTIVE")
    private boolean active;

    @Column(name = "DELETED_AT")
    private LocalDate deleteAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FARMER_GUID", referencedColumnName = "FARMER_GUID", nullable = false)
    private FarmerEntity farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_GUID", referencedColumnName = "CATEGORY_GUID", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_CATEGORY_GUID", referencedColumnName = "SUB_CATEGORY_GUID", nullable = false)
    private SubCategoryEntity subCategory;
}
