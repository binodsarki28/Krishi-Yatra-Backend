package com.krishiYatra.krishiYatra.Service;

import com.krishiYatra.krishiYatra.address.AddressEntity;
import com.krishiYatra.krishiYatra.common.enums.VerificationStatus;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.farmer.FarmerEntity;
import com.krishiYatra.krishiYatra.farmer.FarmerRepo;
import com.krishiYatra.krishiYatra.stock.*;
import com.krishiYatra.krishiYatra.stock.category.CategoryEntity;
import com.krishiYatra.krishiYatra.stock.category.CategoryRepo;
import com.krishiYatra.krishiYatra.stock.dto.StockRequestDto;
import com.krishiYatra.krishiYatra.stock.dto.StockResponseDto;
import com.krishiYatra.krishiYatra.stock.mapper.StockMapper;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryEntity;
import com.krishiYatra.krishiYatra.stock.subCategory.SubCategoryRepo;
import com.krishiYatra.krishiYatra.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock private StockRepo stockRepo;
    @Mock private FarmerRepo farmerRepo;
    @Mock private CategoryRepo categoryRepo;
    @Mock private SubCategoryRepo subCategoryRepo;
    @Mock private StockMapper stockMapper;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private StockService stockService;

    private FarmerEntity mockFarmer;
    private UserEntity mockUser;
    private CategoryEntity mockCategory;
    private SubCategoryEntity mockSubCategory;
    private StockRequestDto mockDto;
    private StockEntity mockStock;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setUserId(UUID.randomUUID().toString());
        mockUser.setUsername("farmer1");
        mockUser.setAddress(new AddressEntity());

        mockFarmer = new FarmerEntity();
        mockFarmer.setFarmerId(UUID.randomUUID().toString());
        mockFarmer.setUser(mockUser);
        mockFarmer.setStatus(VerificationStatus.VERIFIED);

        mockCategory = new CategoryEntity();
        mockCategory.setCategoryId(1);
        mockCategory.setCategoryName("Grains");

        mockSubCategory = new SubCategoryEntity();
        mockSubCategory.setSubCategoryId(1);
        mockSubCategory.setSubCategoryName("Rice");

        mockDto = new StockRequestDto();
        mockDto.setStockName("Basmati Rice");
        mockDto.setProductName("Rice");
        mockDto.setCategoryId(1);
        mockDto.setSubCategoryId(1);
        mockDto.setQuantity(100.0);
        mockDto.setMinQuantity(10);
        mockDto.setPricePerUnit(50.0);

        mockStock = new StockEntity();
        mockStock.setStockId(UUID.randomUUID().toString());
        mockStock.setStockName("Basmati Rice");
        mockStock.setFarmer(mockFarmer);
        mockStock.setStockSlug("basmati-rice");

        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuth() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("farmer1");
        when(farmerRepo.findByUser_Username("farmer1")).thenReturn(Optional.of(mockFarmer));
    }

    @Test
    void createStock_Success() {
        mockAuth();
        when(categoryRepo.findById(1)).thenReturn(Optional.of(mockCategory));
        when(subCategoryRepo.findById(1)).thenReturn(Optional.of(mockSubCategory));
        when(stockMapper.toEntity(any())).thenReturn(new StockEntity());

        ServerResponse response = stockService.createStock(mockDto, null);

        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertEquals(StockConst.CREATE_STOCK, response.getMessage());
        verify(stockRepo).save(any());
    }

    @Test
    void createStock_UnverifiedFarmer_Failure() {
        mockAuth();
        mockFarmer.setStatus(VerificationStatus.PENDING);

        ServerResponse response = stockService.createStock(mockDto, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
        assertEquals(StockConst.FARMER_NOT_VERIFIED, response.getMessage());
    }

    @Test
    void createStock_FarmerNoAddress_Failure() {
        mockAuth();
        mockUser.setAddress(null);

        ServerResponse response = stockService.createStock(mockDto, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getHttpStatus());
        assertEquals(StockConst.ADDRESS_REQUIRED, response.getMessage());
    }

    @Test
    void createStock_MinQuantityGreaterThanStock_Failure() {
        mockAuth();
        mockDto.setMinQuantity(200);
        when(categoryRepo.findById(1)).thenReturn(Optional.of(mockCategory));
        when(subCategoryRepo.findById(1)).thenReturn(Optional.of(mockSubCategory));

        ServerResponse response = stockService.createStock(mockDto, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
        assertEquals(StockConst.MIN_GREATER_THAN_QUANTITY, response.getMessage());
    }

    @Test
    void updateStock_Success() {
        mockAuth();
        mockDto.setStockSlug("basmati-rice");
        when(stockRepo.findByStockSlug("basmati-rice")).thenReturn(Optional.of(mockStock));
        when(categoryRepo.findById(1)).thenReturn(Optional.of(mockCategory));
        when(subCategoryRepo.findById(1)).thenReturn(Optional.of(mockSubCategory));

        ServerResponse response = stockService.updateStock(mockDto, null);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(StockConst.UPDATE_STOCK, response.getMessage());
        verify(stockRepo).save(any());
    }

    @Test
    void updateStock_Unauthorized_Failure() {
        mockAuth();
        mockDto.setStockSlug("other-stock");
        StockEntity otherStock = new StockEntity();
        FarmerEntity otherFarmer = new FarmerEntity();
        otherFarmer.setFarmerId("other-id");
        otherStock.setFarmer(otherFarmer);
        
        when(stockRepo.findByStockSlug("other-stock")).thenReturn(Optional.of(otherStock));

        ServerResponse response = stockService.updateStock(mockDto, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
        assertEquals(StockConst.UNAUTHORIZED_UPDATE, response.getMessage());
    }

    @Test
    void deleteStockBySlug_Success() {
        mockAuth();
        when(stockRepo.findByStockSlug("basmati-rice")).thenReturn(Optional.of(mockStock));

        ServerResponse response = stockService.deleteStockBySlug("basmati-rice");

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertFalse(mockStock.isActive());
        verify(stockRepo).save(mockStock);
    }

    @Test
    void getStockBySlug_Success() {
        when(stockRepo.findByStockSlug("basmati-rice")).thenReturn(Optional.of(mockStock));
        when(stockMapper.toResponseDto(mockStock)).thenReturn(new StockResponseDto());

        ServerResponse response = stockService.getStockBySlug("basmati-rice");

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(StockConst.FETCH_STOCK, response.getMessage());
    }

    @Test
    void toggleStockStatus_Success() {
        mockStock.setActive(true);
        when(stockRepo.findByStockSlug("basmati-rice")).thenReturn(Optional.of(mockStock));

        ServerResponse response = stockService.toggleStockStatus("basmati-rice");

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertFalse(mockStock.isActive());
    }

    @Test
    void createCategory_Success() {
        ServerResponse response = stockService.createCategory("Fruits");
        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertEquals(StockConst.CATEGORY_CREATED, response.getMessage());
        verify(categoryRepo).save(any());
    }

    @Test
    void createSubCategory_Success() {
        when(categoryRepo.findById(1)).thenReturn(Optional.of(mockCategory));
        ServerResponse response = stockService.createSubCategory(1, "Apple");
        
        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertEquals(StockConst.SUB_CATEGORY_CREATED, response.getMessage());
        verify(subCategoryRepo).save(any());
    }

    @Test
    void getCategories_Success() {
        when(categoryRepo.findAll()).thenReturn(Collections.singletonList(mockCategory));
        mockCategory.setActive(true);
        
        ServerResponse response = stockService.getCategories();
        assertEquals(HttpStatus.OK, response.getHttpStatus());
    }

    @Test
    void getSubCategories_Success() {
        when(subCategoryRepo.findByCategory_CategoryIdAndActiveTrue(1)).thenReturn(Collections.emptyList());
        
        ServerResponse response = stockService.getSubCategories(1);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(StockConst.SUB_CATEGORY_FETCHED, response.getMessage());
    }
}
