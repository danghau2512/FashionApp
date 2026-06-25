package com.example.fashionshop.controller;

import com.example.fashionshop.dto.CreateProductReviewRequest;
import com.example.fashionshop.dto.ProductReviewResponse;
import com.example.fashionshop.dto.ReviewEligibilityResponse;
import com.example.fashionshop.service.ProductReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductReviewResponse> getReviewsByProductId(@PathVariable Long productId) {
        return productReviewService.getReviewsByProductId(productId);
    }

    @GetMapping("/product/{productId}/eligibility")
    public ReviewEligibilityResponse getReviewEligibility(@PathVariable Long productId,
                                                          @RequestParam Long userId) {
        return productReviewService.getReviewEligibility(userId, productId);
    }

    @PostMapping
    public ProductReviewResponse createReview(@Valid @RequestBody CreateProductReviewRequest request) {
        return productReviewService.createReview(request);
    }
}