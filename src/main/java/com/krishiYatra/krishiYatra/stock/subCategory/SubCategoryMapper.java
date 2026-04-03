package com.krishiYatra.krishiYatra.stock.subCategory;

import com.krishiYatra.krishiYatra.stock.subCategory.dto.SubCategoryRequest;
import com.krishiYatra.krishiYatra.stock.subCategory.dto.SubCategoryResponse;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class SubCategoryMapper {

    public SubCategoryEntity toEntity(SubCategoryRequest request, CategoryEntity category) {
        SubCategoryEntity entity = new SubCategoryEntity();
        entity.setSubCategoryName(request.getName());
        entity.setCategory(category);
        return entity;
    }

    public SubCategoryResponse toResponse(SubCategoryEntity entity) {
        SubCategoryResponse response = new SubCategoryResponse();
        response.setSubCategoryId(entity.getSubCategoryId());
        response.setSubCategoryName(entity.getSubCategoryName());
        if (entity.getCategory() != null) {
            response.setCategoryId(entity.getCategory().getCategoryId());
            response.setCategoryName(entity.getCategory().getCategoryName());
        }
        response.setActive(entity.isActive());
        return response;
    }
}
