package com.krishiYatra.krishiYatra.stock.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class StockRequestDto {
    private String stockSlug; // Optional for create, required for update logic

    @NotBlank(message = "Stock name is required")
    private String stockName;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;
    private List<String> stockImages;
    
    @JsonSetter("stockImages")
    public void setStockImages(Object value) {
        if (value instanceof String) {
            String s = (String) value;
            if (s.isEmpty()) {
                this.stockImages = new ArrayList<>();
            } else {
                // If it's a comma-separated string, handle it
                this.stockImages = Arrays.asList(s.split(",\\s*"));
            }
        } else if (value instanceof List) {
            this.stockImages = (List<String>) value;
        }
    }

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @NotNull(message = "Price per unit is required")
    @Positive(message = "Price must be positive")
    private Double pricePerUnit;

    @NotNull(message = "Minimum quantity is required")
    @Positive(message = "Minimum quantity must be at least 1")
    private Integer minQuantity = 1;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    @NotNull(message = "Sub-category is required")
    private Integer subCategoryId;
}
