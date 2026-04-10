package com.krishiYatra.krishiYatra.stock.category;

import com.krishiYatra.krishiYatra.stock.category.dto.CategoryRequest;
import com.krishiYatra.krishiYatra.stock.category.dto.CategoryResponse;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.dto.SubCategoryResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryEntity toEntity(CategoryRequest request) {
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryName(request.getName());
        return entity;
    }

    public CategoryResponse toResponse(CategoryEntity entity) {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(entity.getCategoryId());
        response.setCategoryName(entity.getCategoryName());
        response.setActive(entity.isActive());
        if (entity.getSubCategories() != null) {
            response.setSubCategories(entity.getSubCategories().stream()
                    .map(this::toSubCategoryResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    private SubCategoryResponse toSubCategoryResponse(SubCategoryEntity entity) {
        SubCategoryResponse response = new SubCategoryResponse();
        response.setSubCategoryId(entity.getSubCategoryId());
        response.setSubCategoryName(entity.getSubCategoryName());
        response.setCategoryId(entity.getCategory().getCategoryId());
        response.setCategoryName(entity.getCategory().getCategoryName());
        response.setActive(entity.isActive());
        return response;
    }
}
