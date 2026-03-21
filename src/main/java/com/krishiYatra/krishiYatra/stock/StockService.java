package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryRepo;
import com.krishiYatra.krishiYatra.stock.dao.IStockDao;
import com.krishiYatra.krishiYatra.stock.dto.*;
import com.krishiYatra.krishiYatra.stock.mapper.StockMapper;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryRepo;
import lombok.RequiredArgsConstructor;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final com.krishiYatra.krishiYatra.config.CloudinaryService cloudinaryService;

    private FarmerEntity getCurrentFarmer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return farmerRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Farmer record not found for user: " + username));
    }

    @Transactional
    public ServerResponse createStock(StockRequestDto dto, MultipartFile[] images) {
        FarmerEntity farmer = getCurrentFarmer();
        if (farmer.getStatus() != VerificationStatus.VERIFIED) {
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

        if (images != null && images.length > 0) {
            log.info("Creating stock with {} images", images.length);
            List<String> imageUrls = new ArrayList<>();
            for (var img : images) {
                if (img != null && !img.isEmpty()) {
                    try {
                        String url = cloudinaryService.uploadFile(img, "stocks");
                        imageUrls.add(url);
                        log.info("Uploaded to Cloudinary: {}", url);
                    } catch (Exception e) {
                        log.error("Failed to upload stock image", e);
                    }
                }
            }
            List<StockImageEntity> batch = new ArrayList<>();
            for (int i = 0; i < imageUrls.size(); i++) {
                batch.add(new StockImageEntity(imageUrls.get(i), entity, i));
            }
            entity.setStockImages(batch);
        }

        stockRepo.save(entity);
        return ServerResponse.successResponse(StockConst.CREATE_STOCK, HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse updateStock(StockRequestDto dto, MultipartFile[] images) {
        if (dto.getStockSlug() == null || dto.getStockSlug().isEmpty()) {
            return ServerResponse.failureResponse("Stock slug is required for update.", HttpStatus.BAD_REQUEST);
        }

        FarmerEntity farmer = getCurrentFarmer();
        if (farmer.getStatus() != VerificationStatus.VERIFIED) {
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

        if (images != null && images.length > 0) {
            log.info("Updating stock {} with {} images", entity.getStockName(), images.length);
            List<String> imageUrls = new ArrayList<>();
            for (var img : images) {
                if (img != null && !img.isEmpty()) {
                    try {
                        String url = cloudinaryService.uploadFile(img, "stocks");
                        imageUrls.add(url);
                        log.info("Uploaded to Cloudinary: {}", url);
                    } catch (Exception e) {
                        log.error("Failed to upload stock image during update", e);
                    }
                }
            }
            if (!imageUrls.isEmpty()) {
                System.out.println("DEBUG (Service): Successfully uploaded " + imageUrls.size() + " photos to Cloudinary");
                entity.getStockImages().clear();
                for (int i = 0; i < imageUrls.size(); i++) {
                    entity.getStockImages().add(new StockImageEntity(imageUrls.get(i), entity, i));
                }
            }
        }
        
        System.out.println("DEBUG (Final): Stock Entity now has " + entity.getStockImages().size() + " image rows");
        stockRepo.save(entity);
        return ServerResponse.successResponse(StockConst.UPDATE_STOCK, HttpStatus.OK);
    }

    @Transactional
    public ServerResponse deleteStockBySlug(String slug) {
        FarmerEntity farmer = getCurrentFarmer();
        var entityOptional = stockRepo.findByStockSlug(slug);
        if (entityOptional.isEmpty()) {
            return ServerResponse.failureResponse(StockConst.STOCK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        StockEntity entity = entityOptional.get();

        if (!entity.getFarmer().getFarmerId().equals(farmer.getFarmerId())) {
            return ServerResponse.failureResponse("You are not authorized to delete this stock.", HttpStatus.UNAUTHORIZED);
        }

        entity.setActive(false);
        stockRepo.save(entity);

        return ServerResponse.successResponse(StockConst.DELETE_STOCK, HttpStatus.OK);
    }

    public ServerResponse getStockBySlug(String slug) {
        var entityOptional = stockRepo.findByStockSlug(slug);
        if (entityOptional.isEmpty()) {
            return ServerResponse.failureResponse(StockConst.STOCK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        StockEntity entity = entityOptional.get();

        StockResponseDto responseDto = stockMapper.toResponseDto(entity);
        return ServerResponse.successObjectResponse(StockConst.FETCH_STOCK, HttpStatus.OK, responseDto);
    }

    @Transactional
    public ServerResponse toggleStockStatus(String slug) {
        var entityOptional = stockRepo.findByStockSlug(slug);
        if (entityOptional.isEmpty()) {
            return ServerResponse.failureResponse(StockConst.STOCK_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        StockEntity entity = entityOptional.get();

        entity.setActive(!entity.isActive());
        stockRepo.save(entity);

        return ServerResponse.successResponse("Stock status updated successfully.", HttpStatus.OK);
    }

    public ServerResponse getStockList(Map<String, String> params) {
        List<StockListResponse> stocks = stockDao.getAllStocks(params);
        return ServerResponse.successObjectResponse(StockConst.FETCH_STOCK, HttpStatus.OK, stocks);
    }

    public ServerResponse getFarmerStocks() {
        FarmerEntity farmer = getCurrentFarmer();
        List<StockEntity> stocks = stockRepo.findByFarmer_FarmerId(farmer.getFarmerId());
        List<StockListResponse> response = stocks.stream()
                .map(stockMapper::toListResponse)
                .collect(Collectors.toList());
        return ServerResponse.successObjectResponse(StockConst.FETCH_STOCK, HttpStatus.OK, response);
    }

    @Transactional
    public ServerResponse createCategory(String name) {
        CategoryEntity category = new CategoryEntity();
        category.setCategoryName(name);
        categoryRepo.save(category);
        return ServerResponse.successResponse("Category created successfully", HttpStatus.CREATED);
    }

    @Transactional
    public ServerResponse createSubCategory(String categoryId, String name) {
        CategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        SubCategoryEntity subCategory = new SubCategoryEntity();
        subCategory.setSubCategoryName(name);
        subCategory.setCategory(category);
        subCategoryRepo.save(subCategory);
        return ServerResponse.successResponse("Sub-category created successfully", HttpStatus.CREATED);
    }

    public ServerResponse getCategories() {
        List<CategoryResponseDto> categories = categoryRepo.findAll().stream()
                .map(stockMapper::toCategoryDto)
                .collect(Collectors.toList());
        return ServerResponse.successObjectResponse("Categories fetched successfully", HttpStatus.OK, categories);
    }

    public ServerResponse getSubCategories(String categoryId) {
        List<SubCategoryEntity> subCategories;
        if (categoryId != null && !categoryId.isEmpty()) {
            subCategories = subCategoryRepo.findByCategory_CategoryId(categoryId);
        } else {

            subCategories = subCategoryRepo.findAll();
        }
        List<SubCategoryResponseDto> response = subCategories.stream()
                .map(stockMapper::toSubCategoryDto)
                .collect(Collectors.toList());
        return ServerResponse.successObjectResponse("Sub-categories fetched successfully", HttpStatus.OK, response);
    }

    @Transactional
    public ServerResponse adjustStockQuantity(String slug, Double amount) {
        StockEntity entity = stockRepo.findByStockSlug(slug)
                .orElseThrow(() -> new RuntimeException(StockConst.STOCK_NOT_FOUND));
        
        entity.setQuantity(entity.getQuantity() + amount);
        if (entity.getQuantity() < 0) entity.setQuantity(0.0);
        
        stockRepo.save(entity);
        return ServerResponse.successResponse("Stock quantity adjusted", HttpStatus.OK);
    }
}
