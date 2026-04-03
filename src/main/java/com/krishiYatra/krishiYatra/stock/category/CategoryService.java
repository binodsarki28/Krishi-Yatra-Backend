package com.krishiYatra.krishiYatra.stock.category;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.category.dto.CategoryRequest;
import com.krishiYatra.krishiYatra.stock.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final CategoryMapper categoryMapper;

    @Transactional
    public ServerResponse createCategory(CategoryRequest request) {
        String formattedName = capitalizeFirstLetter(request.getName());
        
        // Case-insensitive exact match check (You could write a custom query in repo, or simply stream)
        boolean exists = categoryRepo.findAll().stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase(formattedName));
                
        if (exists) {
            return ServerResponse.failureResponse(CategoryConst.CATEGORY_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryName(formattedName);
        CategoryEntity saved = categoryRepo.save(entity);
        return ServerResponse.successObjectResponse(CategoryConst.CATEGORY_CREATED, HttpStatus.CREATED, categoryMapper.toResponse(saved));
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        input = input.trim();
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public ServerResponse getAllCategories() {
        List<CategoryResponse> categories = categoryRepo.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
        return ServerResponse.successObjectResponse(CategoryConst.CATEGORY_FETCHED, HttpStatus.OK, categories);
    }

    @Transactional
    public ServerResponse deleteCategory(int id) {
        CategoryEntity category = categoryRepo.findById(id).orElse(null);
        if (category == null) {
            return ServerResponse.failureResponse(CategoryConst.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        category.setActive(!category.isActive());
        
        if (category.getSubCategories() != null) {
            for (com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity sub : category.getSubCategories()) {
                sub.setActive(category.isActive());
            }
        }

        categoryRepo.save(category);
        return ServerResponse.successResponse(category.isActive() ? "Category restored" : CategoryConst.CATEGORY_DELETED, HttpStatus.OK);
    }
}
