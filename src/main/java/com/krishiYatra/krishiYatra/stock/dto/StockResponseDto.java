package com.krishiYatra.krishiYatra.stock.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class StockResponseDto {
    private String stockName;
    private String productName;
    private String stockSlug;
    private String description;
    private List<String> stockImages; // Updated to List
    private Double quantity;
    private Double pricePerUnit;
    private String categoryName;
    private String categoryId;
    private String subCategoryName;
    private String subCategoryId;
    private String farmerName;
    private Integer minQuantity;
    private boolean active;
}
