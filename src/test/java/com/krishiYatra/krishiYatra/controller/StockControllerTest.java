package com.krishiYatra.krishiYatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtAuthenticationFilter;
import com.krishiYatra.krishiYatra.config.security.jwt.JwtTokenProvider;
import com.krishiYatra.krishiYatra.config.security.service.CustomUserDetailsService;
import com.krishiYatra.krishiYatra.stock.StockConst;
import com.krishiYatra.krishiYatra.stock.StockController;
import com.krishiYatra.krishiYatra.stock.StockService;
import com.krishiYatra.krishiYatra.stock.dto.StockRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StockControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private StockService stockService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Endpoint: Create Stock Success (Multipart)")
    void createStock_ReturnCreated() throws Exception {
        StockRequestDto dto = new StockRequestDto();
        dto.setStockName("Rice");
        String json = objectMapper.writeValueAsString(dto);

        MockMultipartFile stockData = new MockMultipartFile("stockData", "", "application/json", json.getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "image data".getBytes());

        when(stockService.createStock(any(), any())).thenReturn(ServerResponse.successResponse(StockConst.CREATE_STOCK, HttpStatus.CREATED));

        mockMvc.perform(multipart("/api/v1/stock/create")
                .file(stockData)
                .file(image))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(StockConst.CREATE_STOCK));
    }

    @Test
    @DisplayName("Endpoint: Get Stock Details Success")
    void getStockDetails_ReturnOk() throws Exception {
        when(stockService.getStockBySlug("basmati-rice")).thenReturn(ServerResponse.successResponse(StockConst.FETCH_STOCK, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/stock/details/basmati-rice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(StockConst.FETCH_STOCK));
    }

    @Test
    @DisplayName("Endpoint: Toggle Stock Status Success")
    void deleteOrUndeleteStock_ReturnOk() throws Exception {
        when(stockService.toggleStockStatus("basmati-rice")).thenReturn(ServerResponse.successResponse(StockConst.STOCK_STATUS_UPDATED, HttpStatus.OK));

        mockMvc.perform(put("/api/v1/stock/delete-or-undelete/basmati-rice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(StockConst.STOCK_STATUS_UPDATED));
    }

    @Test
    @DisplayName("Endpoint: Get Stock List Success")
    void getStockList_ReturnOk() throws Exception {
        when(stockService.getStockList(any())).thenReturn(ServerResponse.successResponse(StockConst.FETCH_STOCK, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/stock/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(StockConst.FETCH_STOCK));
    }

    @Test
    @DisplayName("Endpoint: Create Category Success")
    void createCategory_ReturnCreated() throws Exception {
        when(stockService.createCategory("Fruits")).thenReturn(ServerResponse.successResponse(StockConst.CATEGORY_CREATED, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/stock/category/create")
                .param("name", "Fruits"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Endpoint: Get Categories Success")
    void getCategories_ReturnOk() throws Exception {
        when(stockService.getCategories()).thenReturn(ServerResponse.successResponse(StockConst.CATEGORIES_FETCHED, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/stock/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(StockConst.CATEGORIES_FETCHED));
    }

    @Test
    @DisplayName("Endpoint: Get Subcategories Success")
    void getSubCategories_ReturnOk() throws Exception {
        when(stockService.getSubCategories(any())).thenReturn(ServerResponse.successResponse(StockConst.SUB_CATEGORY_FETCHED, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/stock/subcategories")
                .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(StockConst.SUB_CATEGORY_FETCHED));
    }
}
