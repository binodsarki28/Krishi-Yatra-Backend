package com.krishiYatra.krishiYatra.stock.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockResponseDto {
    private String stockName;
    private String productName;
    private String stockSlug;
    private String description;
    private String stockImages;
    private Double quantity;
    private Double pricePerUnit;
    private String categoryName;
    private String categoryId;
    private String subCategoryName;
    private String subCategoryId;
    private String farmerName;
}
