package com.krishiYatra.krishiYatra.demand.mapper;

import com.krishiYatra.krishiYatra.demand.DemandEntity;
import com.krishiYatra.krishiYatra.demand.dto.DemandCreateRequest;
import com.krishiYatra.krishiYatra.demand.dto.DemandResponse;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.buyer.BuyerEntity;
import org.springframework.stereotype.Service;

@Service
public class DemandMapper {

    public DemandEntity toEntity(DemandCreateRequest request, CategoryEntity category, SubCategoryEntity subCategory, BuyerEntity buyer) {
        if (request == null) return null;
        DemandEntity entity = new DemandEntity();
        entity.setCategory(category);
        entity.setSubCategory(subCategory);
        entity.setQuantity(request.getQuantity());
        entity.setExpectedPricePerUnit(request.getExpectedPricePerUnit());
        entity.setDescription(request.getDescription());
        entity.setBuyer(buyer);
        return entity;
    }

    public DemandResponse toResponse(DemandEntity entity) {
        if (entity == null) return null;
        DemandResponse response = new DemandResponse();
        response.setDemandId(entity.getDemandId());
        
        if (entity.getCategory() != null) {
            response.setCategoryName(entity.getCategory().getCategoryName());
            response.setCategoryGuid(entity.getCategory().getCategoryId());
        }
        
        if (entity.getSubCategory() != null) {
            response.setSubCategoryName(entity.getSubCategory().getSubCategoryName());
            response.setSubCategoryGuid(entity.getSubCategory().getSubCategoryId());
        }
        
        response.setQuantity(entity.getQuantity());
        response.setExpectedPricePerUnit(entity.getExpectedPricePerUnit());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        
        if (entity.getBuyer() != null && entity.getBuyer().getUser() != null) {
            response.setBuyerName(entity.getBuyer().getUser().getFullName());
            response.setBuyerPhone(entity.getBuyer().getUser().getPhoneNumber());
        }
        
        if (entity.getAcceptedBy() != null && entity.getAcceptedBy().getUser() != null) {
            response.setAcceptedFarmerName(entity.getAcceptedBy().getUser().getFullName());
        }
        
        if (entity.getFulfilledStock() != null) {
            response.setFulfilledStockSlug(entity.getFulfilledStock().getStockSlug());
        }
        
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
