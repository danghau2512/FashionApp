package com.example.fashionshop.controller;

import com.example.fashionshop.dto.ProductVariantResponse;
import com.example.fashionshop.service.ProductVariantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping("/products/{productId}/variants")
    public List<ProductVariantResponse> getVariantsByProductId(@PathVariable Long productId) {
        return productVariantService.getVariantsByProductId(productId);
    }

    @GetMapping("/variants/{id}")
    public ProductVariantResponse getVariantById(@PathVariable Long id) {
        return productVariantService.getVariantById(id);
    }
}