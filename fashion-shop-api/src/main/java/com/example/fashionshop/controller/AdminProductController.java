package com.example.fashionshop.controller;

import com.example.fashionshop.dto.AdminProductRequest;
import com.example.fashionshop.dto.AdminProductResponse;
import com.example.fashionshop.dto.UpdateProductStatusRequest;
import com.example.fashionshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin("*")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<AdminProductResponse> getAdminProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId
    ) {
        return productService.getAdminProducts(keyword, status, categoryId);
    }


    @GetMapping("/{id}")
    public AdminProductResponse getAdminProductById(@PathVariable Long id) {
        return productService.getAdminProductById(id);
    }

    @PostMapping
    public AdminProductResponse createProduct(
            @RequestParam Long adminId,
            @Valid @RequestBody AdminProductRequest request
    ) {
        return productService.createProduct(request, adminId);
    }

    @PutMapping("/{id}")
    public AdminProductResponse updateProduct(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @Valid @RequestBody AdminProductRequest request
    ) {
        return productService.updateProduct(id, request, adminId);
    }

    @PutMapping("/{id}/status")
    public AdminProductResponse updateProductStatus(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @Valid @RequestBody UpdateProductStatusRequest request
    ) {
        return productService.updateProductStatus(id, request.getStatus(), adminId);
    }
}