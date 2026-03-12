package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryRepo;
import com.krishiYatra.krishiYatra.stock.dao.IStockDao;
import com.krishiYatra.krishiYatra.stock.dto.StockRequestDto;
import com.krishiYatra.krishiYatra.stock.dto.StockListResponse;
import com.krishiYatra.krishiYatra.stock.dto.StockResponseDto;
import com.krishiYatra.krishiYatra.stock.mapper.StockMapper;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final StockRepo stockRepo;
    private final FarmerRepo farmerRepo;
    private final CategoryRepo categoryRepo;
    private final SubCategoryRepo subCategoryRepo;
    private final StockMapper stockMapper;
    private final IStockDao stockDao;

    private FarmerEntity getCurrentFarmer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return farmerRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Farmer record not found for user: " + username));
    }

    @Transactional
    public ServerResponse createStock(StockRequestDto dto) {
        FarmerEntity farmer = getCurrentFarmer();
        if (!farmer.isVerified()) {
            return ServerResponse.failureResponse(StockConst.FARMER_NOT_VERIFIED, HttpStatus.FORBIDDEN);
        }

        CategoryEntity category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        SubCategoryEntity subCategory = subCategoryRepo.findById(dto.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("Sub-category not found"));

        StockEntity entity = stockMapper.toEntity(dto);
        entity.setFarmer(farmer);
        entity.setCategory(category);
        entity.setSubCategory(subCategory);

        stockRepo.save(entity);
        return ServerResponse.successResponse(StockConst.CREATE_STOCK, HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse updateStock(StockRequestDto dto) {
        if (dto.getStockSlug() == null || dto.getStockSlug().isEmpty()) {
            return ServerResponse.failureResponse("Stock slug is required for update.", HttpStatus.BAD_REQUEST);
        }

        FarmerEntity farmer = getCurrentFarmer();
        if (!farmer.isVerified()) {
            return ServerResponse.failureResponse(StockConst.FARMER_NOT_VERIFIED, HttpStatus.FORBIDDEN);
        }

        StockEntity entity = stockRepo.findByStockSlug(dto.getStockSlug())
                .orElseThrow(() -> new RuntimeException(StockConst.STOCK_NOT_FOUND));

        if (!entity.getFarmer().getFarmerId().equals(farmer.getFarmerId())) {
            return ServerResponse.failureResponse("You are not authorized to update this stock.", HttpStatus.UNAUTHORIZED);
        }

        CategoryEntity category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        SubCategoryEntity subCategory = subCategoryRepo.findById(dto.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("Sub-category not found"));

        stockMapper.updateEntity(entity, dto);
        entity.setCategory(category);
        entity.setSubCategory(subCategory);

        stockRepo.save(entity);
        return ServerResponse.successResponse(StockConst.UPDATE_STOCK, HttpStatus.OK);
    }

    @Transactional
    public ServerResponse deleteStockBySlug(String slug) {
        FarmerEntity farmer = getCurrentFarmer();
        StockEntity entity = stockRepo.findByStockSlug(slug)
                .orElseThrow(() -> new RuntimeException(StockConst.STOCK_NOT_FOUND));

        if (!entity.getFarmer().getFarmerId().equals(farmer.getFarmerId())) {
            return ServerResponse.failureResponse("You are not authorized to delete this stock.", HttpStatus.UNAUTHORIZED);
        }

        stockRepo.delete(entity);
        return ServerResponse.successResponse(StockConst.DELETE_STOCK, HttpStatus.OK);
    }

    public ServerResponse getStockBySlug(String slug) {
        StockEntity entity = stockRepo.findByStockSlug(slug)
                .orElseThrow(() -> new RuntimeException(StockConst.STOCK_NOT_FOUND));

        StockResponseDto responseDto = stockMapper.toResponseDto(entity);
        return ServerResponse.successObjectResponse(StockConst.FETCH_STOCK, HttpStatus.OK, responseDto);
    }

    public ServerResponse getStockList(Map<String, String> params) {
        List<StockListResponse> stocks = stockDao.getAllStocks(params);
        return ServerResponse.successObjectResponse(StockConst.FETCH_STOCK, HttpStatus.OK, stocks, stocks.size());
    }

    public ServerResponse getFarmerStocks() {
        FarmerEntity farmer = getCurrentFarmer();
        Map<String, String> params = new HashMap<>();
        params.put("farmerId", farmer.getFarmerId());
        params.put("all", "true"); 
        
        List<StockListResponse> stocks = stockDao.getAllStocks(params);
        return ServerResponse.successObjectResponse(StockConst.FETCH_STOCK, HttpStatus.OK, stocks, stocks.size());
    }

    @Transactional
    public ServerResponse createCategory(String name) {
        CategoryEntity cat = new CategoryEntity();
        cat.setCategoryName(name);
        categoryRepo.save(cat);
        return ServerResponse.successResponse("Category created successfully", HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse createSubCategory(String catId, String name) {
        CategoryEntity cat = categoryRepo.findById(catId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        SubCategoryEntity sub = new SubCategoryEntity();
        sub.setSubCategoryName(name);
        sub.setCategory(cat);
        subCategoryRepo.save(sub);
        return ServerResponse.successResponse("Sub-category created successfully", HttpStatus.CREATED);
    }

    public ServerResponse getCategories() {
        List<com.krishiYatra.krishiYatra.stock.dto.CategoryResponseDto> dtos = categoryRepo.findAll().stream()
                .map(stockMapper::toCategoryDto)
                .toList();
        return ServerResponse.successObjectResponse("Categories fetched", HttpStatus.OK, dtos);
    }

    public ServerResponse getSubCategories(String catId) {
        if (catId != null) {
            CategoryEntity cat = categoryRepo.findById(catId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            List<com.krishiYatra.krishiYatra.stock.dto.SubCategoryResponseDto> dtos = cat.getSubCategories().stream()
                    .map(stockMapper::toSubCategoryDto)
                    .toList();
            return ServerResponse.successObjectResponse("Sub-categories fetched", HttpStatus.OK, dtos);
        }
        List<com.krishiYatra.krishiYatra.stock.dto.SubCategoryResponseDto> dtos = subCategoryRepo.findAll().stream()
                .map(stockMapper::toSubCategoryDto)
                .toList();
        return ServerResponse.successObjectResponse("All Sub-categories fetched", HttpStatus.OK, dtos);
    }
}
