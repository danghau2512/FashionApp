package com.example.fashionshop.service;

import com.example.fashionshop.dto.ProductResponse;
import com.example.fashionshop.entity.Product;
import com.example.fashionshop.entity.ProductRecommendation;
import com.example.fashionshop.repository.ProductRecommendationRepository;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.UserProductEventRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecommendationService {

    private final ProductRecommendationRepository recommendationRepository;
    private final UserProductEventRepository eventRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public RecommendationService(ProductRecommendationRepository recommendationRepository, UserProductEventRepository eventRepository, ProductRepository productRepository, UserRepository userRepository, ProductService productService) {
        this.recommendationRepository = recommendationRepository;
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    public List<ProductResponse> getRecommendations(Long userId, Integer limit) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        int safeLimit = limit == null ? 8 : Math.max(1, Math.min(limit, 20));
        List<Product> result = new ArrayList<>();
        Set<Long> addedProductIds = new HashSet<>();

        List<ProductRecommendation> recommendations = recommendationRepository.findByUser_IdOrderByRankNumberAsc(userId);

        for (ProductRecommendation recommendation : recommendations) {
            Product product = recommendation.getProduct();

            if (product != null && "ACTIVE".equals(product.getStatus()) && addedProductIds.add(product.getId())) {
                result.add(product);
            }

            if (result.size() >= safeLimit) {
                break;
            }
        }

        if (result.size() < safeLimit) {
            List<Product> popularProducts = eventRepository.findPopularProducts(PageRequest.of(0, safeLimit * 2));

            for (Product product : popularProducts) {
                if (addedProductIds.add(product.getId())) {
                    result.add(product);
                }

                if (result.size() >= safeLimit) {
                    break;
                }
            }
        }

        if (result.size() < safeLimit) {
            for (Product product : productRepository.findByStatus("ACTIVE")) {
                if (addedProductIds.add(product.getId())) {
                    result.add(product);
                }

                if (result.size() >= safeLimit) {
                    break;
                }
            }
        }

        return result.stream().map(productService::toProductResponse).toList();
    }
}