package com.krishiYatra.krishiYatra.stock.category.dto;

import lombok.Data;
import java.util.List;
import com.krishiYatra.krishiYatra.stock.subCategory.dto.SubCategoryResponse;

@Data
public class CategoryResponse {
    private int categoryId;
    private String categoryName;
    private boolean active;
    private List<SubCategoryResponse> subCategories;
}
