package com.krishiYatra.krishiYatra.stock.category;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import com.krishiYatra.krishiYatra.stock.category.dto.CategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create a new category (Admin only)")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> createCategory(@RequestBody CategoryRequest request) {
        ServerResponse response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "List all active categories")
    @GetMapping
    public ResponseEntity<ServerResponse> getAllCategories() {
        ServerResponse response = categoryService.getAllCategories();
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(summary = "Deactivate a category (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ServerResponse> deleteCategory(@PathVariable int id) {
        ServerResponse response = categoryService.deleteCategory(id);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
