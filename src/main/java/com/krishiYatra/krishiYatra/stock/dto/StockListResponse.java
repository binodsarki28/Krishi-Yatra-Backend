package com.krishiYatra.krishiYatra.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockListResponse {
    private String stockName;
    private String productName;
    private String stockSlug;
    private Double quantity;
    private Double pricePerUnit;
    private String subCategoryName;
    private String categoryName;
    private String farmerName;
    private boolean active;
}
