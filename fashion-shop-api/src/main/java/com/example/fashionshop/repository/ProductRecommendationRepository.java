package com.example.fashionshop.repository;

import com.example.fashionshop.entity.ProductRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, Long> {

    List<ProductRecommendation> findByUser_IdOrderByRankNumberAsc(Long userId);
}