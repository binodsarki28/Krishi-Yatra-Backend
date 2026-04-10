package com.krishiYatra.krishiYatra.stock.subCategory;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryRepo;
import com.krishiYatra.krishiYatra.stock.subCategory.dto.SubCategoryRequest;
import com.krishiYatra.krishiYatra.stock.subCategory.dto.SubCategoryResponse;
import com.krishiYatra.krishiYatra.stock.category.CategoryConst;
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
public class SubCategoryService {

    private final SubCategoryRepo subCategoryRepo;
    private final CategoryRepo categoryRepo;
    private final SubCategoryMapper subCategoryMapper;

    @Transactional
    public ServerResponse createSubCategory(SubCategoryRequest request) {
        String formattedName = capitalizeFirstLetter(request.getName());
        
        boolean exists = subCategoryRepo.findAll().stream()
                .anyMatch(s -> s.getSubCategoryName().equalsIgnoreCase(formattedName));
                
        if (exists) {
            return ServerResponse.failureResponse(CategoryConst.SUB_CATEGORY_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        
        CategoryEntity category = categoryRepo.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ServerResponse.failureResponse(CategoryConst.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        
        SubCategoryEntity entity = new SubCategoryEntity();
        entity.setSubCategoryName(formattedName);
        entity.setCategory(category);
        
        SubCategoryEntity saved = subCategoryRepo.save(entity);
        return ServerResponse.successObjectResponse(CategoryConst.SUB_CATEGORY_CREATED, HttpStatus.CREATED, subCategoryMapper.toResponse(saved));
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        input = input.trim();
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public ServerResponse getAllSubCategories() {
        List<SubCategoryResponse> subCategories = subCategoryRepo.findAll().stream()
                .map(subCategoryMapper::toResponse)
                .collect(Collectors.toList());
        return ServerResponse.successObjectResponse(CategoryConst.SUB_CATEGORY_FETCHED, HttpStatus.OK, subCategories);
    }

    @Transactional
    public ServerResponse deleteSubCategory(int id) {
        SubCategoryEntity subCategory = subCategoryRepo.findById(id).orElse(null);
        if (subCategory == null) {
            return ServerResponse.failureResponse(SubCategoryConst.SUB_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        subCategory.setActive(!subCategory.isActive());
        subCategoryRepo.save(subCategory);
        return ServerResponse.successResponse(subCategory.isActive() ? "Sub-Category restored" : CategoryConst.SUB_CATEGORY_DELETED, HttpStatus.OK);
    }
}
