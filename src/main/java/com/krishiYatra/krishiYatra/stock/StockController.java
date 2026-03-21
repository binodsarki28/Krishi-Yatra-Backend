package com.krishiYatra.krishiYatra.stock;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.dto.StockRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Create stock (Verified Farmer only)")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> createStock(@Valid @RequestBody StockRequestDto requestDto) {
        ServerResponse response = stockService.createStock(requestDto);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Update stock (Verified Farmer owner only, uses slug in body)")
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> updateStock(@Valid @RequestBody StockRequestDto requestDto) {
        ServerResponse response = stockService.updateStock(requestDto);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Delete stock (Farmer owner only, uses slug)")
    @DeleteMapping("/delete/{slug}")
    @PreAuthorize("hasAuthority('FARMER')")
    public ResponseEntity<ServerResponse> deleteStock(@PathVariable String slug) {
        ServerResponse response = stockService.deleteStockBySlug(slug);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Get stock details by slug")
    @GetMapping("/details/{slug}")
    public ResponseEntity<ServerResponse> getStockDetails(@PathVariable String slug) {
        ServerResponse response = stockService.getStockBySlug(slug);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Toggle stock active status (Admin only)")
    @PutMapping("/toggle-status/{slug}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> toggleStockStatus(@PathVariable String slug) {
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
    public ResponseEntity<ServerResponse> createSubCategory(@RequestParam String categoryId, @RequestParam String name) {
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
    public ResponseEntity<ServerResponse> getSubCategories(@RequestParam(required = false) String categoryId) {
        ServerResponse response = stockService.getSubCategories(categoryId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
