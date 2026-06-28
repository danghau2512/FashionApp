package com.example.fashionshop.controller;

import com.example.fashionshop.dto.ProductResponse;
import com.example.fashionshop.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin("*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/users/{userId}")
    public List<ProductResponse> getRecommendations(@PathVariable Long userId, @RequestParam(defaultValue = "8") Integer limit) {
        return recommendationService.getRecommendations(userId, limit);
    }
}