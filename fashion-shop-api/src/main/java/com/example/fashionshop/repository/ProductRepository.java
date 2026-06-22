package com.example.fashionshop.repository;

import com.example.fashionshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(String status);

    List<Product> findByNameContainingIgnoreCaseAndStatus(String keyword, String status);

    List<Product> findByCategory_IdAndStatus(Long categoryId, String status);

    Long countByStatus(String status);

    @Query("""
        SELECT COALESCE(SUM(p.soldCount), 0)
        FROM Product p
    """)
    Long getTotalSoldQuantity();
}