package com.krishiYatra.krishiYatra.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryResponseDto {
    private int subCategoryId;
    private String subCategoryName;
    private Integer categoryId;
    private String categoryName;
}
