package com.krishiYatra.krishiYatra.demand.dto;

import com.krishiYatra.krishiYatra.common.enums.DemandStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DemandResponse {
    private String demandId;
    private String categoryName;
    private String categoryGuid;
    private String subCategoryName;
    private String subCategoryGuid;
    private Double quantity;
    private Double expectedPricePerUnit;
    private String description;
    private DemandStatus status;
    private String buyerName;
    private String buyerPhone;
    private String acceptedFarmerName;
    private String fulfilledStockSlug;
    private LocalDateTime createdAt;
}
