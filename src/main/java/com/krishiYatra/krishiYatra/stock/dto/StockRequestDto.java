package com.krishiYatra.krishiYatra.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockRequestDto {
    private String stockSlug; // Optional for create, required for update logic

    @NotBlank(message = "Stock name is required")
    private String stockName;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;
    private String stockImages;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @NotNull(message = "Price per unit is required")
    @Positive(message = "Price must be positive")
    private Double pricePerUnit;

    @NotBlank(message = "Category is required")
    private String categoryId;

    @NotBlank(message = "Sub-category is required")
    private String subCategoryId;
}
