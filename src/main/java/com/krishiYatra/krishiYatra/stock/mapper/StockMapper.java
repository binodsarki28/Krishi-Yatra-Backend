package com.krishiYatra.krishiYatra.stock.mapper;

import com.krishiYatra.krishiYatra.stock.StockEntity;
import com.krishiYatra.krishiYatra.stock.dto.StockListResponse;
import com.krishiYatra.krishiYatra.stock.dto.StockRequestDto;
import com.krishiYatra.krishiYatra.stock.dto.StockResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Component
public class StockMapper {

    public StockEntity toEntity(StockRequestDto dto) {
        StockEntity entity = new StockEntity();
        entity.setStockName(dto.getStockName());
        entity.setProductName(dto.getProductName());
        entity.setStockSlug(generateSlug(dto.getStockName()));
        entity.setDescription(dto.getDescription());
        // stockImages is now a List<String> in Entity
        entity.setStockImages(new ArrayList<>()); 
        entity.setQuantity(dto.getQuantity());
        entity.setPricePerUnit(dto.getPricePerUnit());
        entity.setMinQuantity(dto.getMinQuantity());
        entity.setActive(true);
        return entity;
    }

    public void updateEntity(StockEntity entity, StockRequestDto dto) {
        entity.setStockName(dto.getStockName());
        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        // Images are handled separately in Service during upload
        entity.setQuantity(dto.getQuantity());
        entity.setPricePerUnit(dto.getPricePerUnit());
        entity.setMinQuantity(dto.getMinQuantity());
    }

    public StockResponseDto toResponseDto(StockEntity entity) {
        StockResponseDto dto = new StockResponseDto();
        dto.setStockName(entity.getStockName());
        dto.setProductName(entity.getProductName());
        dto.setStockSlug(entity.getStockSlug());
        dto.setDescription(entity.getDescription());
        dto.setStockImages(entity.getStockImageUrls()); // Use helper method
        dto.setQuantity(entity.getQuantity());
        dto.setPricePerUnit(entity.getPricePerUnit());
        dto.setMinQuantity(entity.getMinQuantity());
        
        if (entity.getSubCategory() != null) {
            dto.setSubCategoryId(entity.getSubCategory().getSubCategoryId());
            dto.setSubCategoryName(entity.getSubCategory().getSubCategoryName());
            if (entity.getSubCategory().getCategory() != null) {
                dto.setCategoryId(entity.getSubCategory().getCategory().getCategoryId());
                dto.setCategoryName(entity.getSubCategory().getCategory().getCategoryName());
            }
        }
        
        if (entity.getFarmer() != null) {
            dto.setFarmerName(entity.getFarmer().getUser().getFullName());
        }
        
        dto.setActive(entity.isActive());
        
        return dto;
    }

    public StockListResponse toListResponse(StockEntity entity) {
        StockListResponse dto = new StockListResponse();
        dto.setStockName(entity.getStockName());
        dto.setProductName(entity.getProductName());
        dto.setStockSlug(entity.getStockSlug());
        dto.setQuantity(entity.getQuantity());
        dto.setPricePerUnit(entity.getPricePerUnit());
        dto.setMinQuantity(entity.getMinQuantity());
        dto.setStockImages(entity.getStockImageUrls()); // Use helper method
        dto.setActive(entity.isActive());
        
        if (entity.getSubCategory() != null) {
            dto.setSubCategoryName(entity.getSubCategory().getSubCategoryName());
            if (entity.getSubCategory().getCategory() != null) {
                dto.setCategoryName(entity.getSubCategory().getCategory().getCategoryName());
            }
        }
        
        if (entity.getFarmer() != null) {
            dto.setFarmerName(entity.getFarmer().getUser().getFullName());
        }
        
        return dto;
    }

    public com.krishiYatra.krishiYatra.stock.dto.CategoryResponseDto toCategoryDto(com.krishiYatra.krishiYatra.stock.category.CategoryEntity entity) {
        return new com.krishiYatra.krishiYatra.stock.dto.CategoryResponseDto(entity.getCategoryId(), entity.getCategoryName());
    }

    public com.krishiYatra.krishiYatra.stock.dto.SubCategoryResponseDto toSubCategoryDto(com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity entity) {
        return new com.krishiYatra.krishiYatra.stock.dto.SubCategoryResponseDto(
                entity.getSubCategoryId(),
                entity.getSubCategoryName(),
                entity.getCategory() != null ? entity.getCategory().getCategoryId() : null,
                entity.getCategory() != null ? entity.getCategory().getCategoryName() : null
        );
    }

    private String generateSlug(String name) {
        if (name == null) return UUID.randomUUID().toString();
        String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
        return slug + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
