package com.krishiYatra.krishiYatra.stock.subCategory;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.subCategory.dto.SubCategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sub-categories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @Operation(summary = "Create a new sub-category (Admin only)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> createSubCategory(@RequestBody SubCategoryRequest request) {
        ServerResponse response = subCategoryService.createSubCategory(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "List all active sub-categories")
    @GetMapping
    public ResponseEntity<ServerResponse> getAllSubCategories() {
        ServerResponse response = subCategoryService.getAllSubCategories();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Deactivate a sub-category (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> deleteSubCategory(@PathVariable int id) {
        ServerResponse response = subCategoryService.deleteSubCategory(id);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
