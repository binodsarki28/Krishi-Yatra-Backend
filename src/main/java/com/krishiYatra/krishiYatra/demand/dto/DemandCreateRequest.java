package com.krishiYatra.krishiYatra.demand.dto;

import lombok.Data;

@Data
public class DemandCreateRequest {
    private int categoryId;
    private int subCategoryId;
    private Double quantity;
    private Double expectedPricePerUnit;
    private String description;
}
