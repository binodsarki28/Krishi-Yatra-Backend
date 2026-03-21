package com.krishiYatra.krishiYatra.stock;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "STOCK_IMAGES")
@Getter
@Setter
@NoArgsConstructor
public class StockImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    private Long imageId;

    @Column(name = "IMAGE_URL", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOCK_GUID", nullable = false)
    private StockEntity stock;

    @Column(name = "IMAGE_ORDER")
    private Integer imageOrder;

    public StockImageEntity(String imageUrl, StockEntity stock, Integer imageOrder) {
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.imageOrder = imageOrder;
    }
}
