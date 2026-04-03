package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.dto.StockRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class StockController {

    private final StockService stockService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Operation(summary = "Create stock (Verified Farmer only)")

    @PostMapping(value = "/create", consumes = { org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> createStock(
            org.springframework.web.multipart.MultipartHttpServletRequest request) throws Exception {
        log.info("DIAGNOSTIC - All Part Names: {}", request.getFileMap().keySet());
        
        // Manual parse stockData
        String stockDataJson;
        if (request.getFile("stockData") != null) {
            stockDataJson = new String(request.getFile("stockData").getBytes());
        } else {
            stockDataJson = request.getParameter("stockData");
        }
        
        if (stockDataJson == null || stockDataJson.isEmpty()) {
            return new ResponseEntity<>(ServerResponse.failureResponse("Stock data is missing in the request", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        
        System.out.println("StockController handles create request. Raw JSON: " + stockDataJson);
        StockRequestDto requestDto = objectMapper.readValue(stockDataJson, StockRequestDto.class);
        
        java.util.List<org.springframework.web.multipart.MultipartFile> images = new java.util.ArrayList<>();
        request.getMultiFileMap().forEach((key, list) -> {
            if (key.toLowerCase().contains("image")) {
                images.addAll(list);
            }
        });
        System.out.println("DEBUG (Manual Total): Received " + images.size() + " files total");
        ServerResponse response = stockService.createStock(requestDto, images.toArray(new org.springframework.web.multipart.MultipartFile[0]));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Update stock (Verified Farmer owner only, uses slug in body)")
    @PutMapping(value = "/update", consumes = { org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> updateStock(
            org.springframework.web.multipart.MultipartHttpServletRequest request) throws Exception {
        log.info("DIAGNOSTIC - All Part Names: {}", request.getFileMap().keySet());
        
        // Manual parse stockData
        String stockDataJson;
        if (request.getFile("stockData") != null) {
            stockDataJson = new String(request.getFile("stockData").getBytes());
        } else {
            stockDataJson = request.getParameter("stockData");
        }
        
        if (stockDataJson == null || stockDataJson.isEmpty()) {
            return new ResponseEntity<>(ServerResponse.failureResponse("Stock data is missing in the request", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        
        StockRequestDto requestDto = objectMapper.readValue(stockDataJson, StockRequestDto.class);

        java.util.List<org.springframework.web.multipart.MultipartFile> images = new java.util.ArrayList<>();
        request.getMultiFileMap().forEach((key, list) -> {
            if (key.toLowerCase().contains("image")) {
                images.addAll(list);
            }
        });
        System.out.println("DEBUG (Manual Total): Received " + images.size() + " files total");
        ServerResponse response = stockService.updateStock(requestDto, images.toArray(new org.springframework.web.multipart.MultipartFile[0]));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get stock details by slug")
    @GetMapping("/details/{slug}")
    public ResponseEntity<ServerResponse> getStockDetails(@PathVariable String slug) {
        ServerResponse response = stockService.getStockBySlug(slug);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Soft Delete or Undelete stock (Active/Inactive toggle)")
    @PutMapping("/delete-or-undelete/{slug}")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> deleteOrUndeleteStock(@PathVariable String slug) {
        ServerResponse response = stockService.toggleStockStatus(slug);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "List all stocks with filters (Active only by default)")
    @GetMapping("/list")
    public ResponseEntity<ServerResponse> getStockList(@RequestParam Map<String, String> params) {
        ServerResponse response = stockService.getStockList(params);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get current farmer's stocks")
    @GetMapping("/my-stocks")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> getFarmerStocks() {
        ServerResponse response = stockService.getFarmerStocks();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Create category")
    @PostMapping("/category/create")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> createCategory(@RequestParam String name) {
        ServerResponse response = stockService.createCategory(name);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Create sub-category")
    @PostMapping("/subcategory/create")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> createSubCategory(@RequestParam int categoryId, @RequestParam String name) {
        ServerResponse response = stockService.createSubCategory(categoryId, name);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get categories")
    @GetMapping("/categories")
    public ResponseEntity<ServerResponse> getCategories() {
        ServerResponse response = stockService.getCategories();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get sub-categories by category")
    @GetMapping("/subcategories")
    public ResponseEntity<ServerResponse> getSubCategories(@RequestParam(required = false) Integer categoryId) {
        ServerResponse response = stockService.getSubCategories(categoryId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
