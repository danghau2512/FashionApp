package com.example.fashionshop.repository;

import com.example.fashionshop.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProduct_IdAndStatus(Long productId, String status);

    Optional<ProductVariant> findByIdAndStatus(Long id, String status);

    List<ProductVariant> findByProduct_IdOrderByIdDesc(Long productId);
}