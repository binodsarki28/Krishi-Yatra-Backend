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
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("imageOrder ASC")
    private List<StockImageEntity> stockImages = new ArrayList<>();

    // Helper to get raw URLs as list of strings (for DTOs)
    public List<String> getStockImageUrls() {
        if (stockImages == null) return new ArrayList<>();
        return stockImages.stream()
                .map(StockImageEntity::getImageUrl)
                .collect(java.util.stream.Collectors.toList());
    }

    @Column(name = "QUANTITY")
    private Double quantity;

    @Column(name = "PRICE_PER_UNIT")
    private Double pricePerUnit;

    @Column(name = "MIN_QUANTITY")
    private Integer minQuantity = 1;

    @Column(name = "ACTIVE")
    private boolean active;

    @Column(name = "DELETED_AT")
    private LocalDate deleteAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FARMER_GUID", referencedColumnName = "FARMER_GUID", nullable = false)
    private FarmerEntity farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_CATEGORY_ID", referencedColumnName = "SUB_CATEGORY_ID", nullable = false)
    private SubCategoryEntity subCategory;
}
