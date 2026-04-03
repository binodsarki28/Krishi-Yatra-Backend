package com.krishiYatra.krishiYatra.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockListResponse {
    private String stockName;
    private String productName;
    private String stockSlug;
    private Double quantity;
    private Double pricePerUnit;
    private String subCategoryName;
    private int subCategoryId;
    private String categoryName;
    private int categoryId;
    private String farmerName;
    private Integer minQuantity;
    private List<String> stockImages;
    private boolean active;
}
