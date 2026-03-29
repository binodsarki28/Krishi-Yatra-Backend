package com.krishiYatra.krishiYatra.demand.dto;

import lombok.Data;

@Data
public class DemandCreateRequest {
    private String categoryGuid;
    private String subCategoryGuid;
    private Double quantity;
    private Double expectedPricePerUnit;
    private String description;
}
