package com.example.fashionshop.controller;

import com.example.fashionshop.dto.AdminProductVariantRequest;
import com.example.fashionshop.dto.AdminProductVariantResponse;
import com.example.fashionshop.dto.UpdateProductVariantStatusRequest;
import com.example.fashionshop.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminProductVariantController {

    private final ProductVariantService productVariantService;

    public AdminProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping("/products/{productId}/variants")
    public List<AdminProductVariantResponse> getAdminVariantsByProductId(
            @PathVariable Long productId
    ) {
        return productVariantService.getAdminVariantsByProductId(productId);
    }

    @GetMapping("/variants/{id}")
    public AdminProductVariantResponse getAdminVariantById(
            @PathVariable Long id
    ) {
        return productVariantService.getAdminVariantById(id);
    }

    @PutMapping("/variants/{id}")
    public AdminProductVariantResponse updateVariant(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @Valid @RequestBody AdminProductVariantRequest request
    ) {
        return productVariantService.updateVariant(id, request, adminId);
    }

    @PostMapping("/products/{productId}/variants")
    public AdminProductVariantResponse createVariant(
            @PathVariable Long productId,
            @RequestParam Long adminId,
            @Valid @RequestBody AdminProductVariantRequest request
    ) {
        return productVariantService.createVariant(productId, request, adminId);
    }

    @PutMapping("/variants/{id}/status")
    public AdminProductVariantResponse updateVariantStatus(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @Valid @RequestBody UpdateProductVariantStatusRequest request
    ) {
        return productVariantService.updateVariantStatus(id, request.getStatus(), adminId);
    }
}