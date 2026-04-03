package com.krishiYatra.krishiYatra.stock.subCategory.dto;

import lombok.Data;

@Data
public class SubCategoryResponse {
    private int subCategoryId;
    private String subCategoryName;
    private int categoryId;
    private String categoryName;
    private boolean active;
}
