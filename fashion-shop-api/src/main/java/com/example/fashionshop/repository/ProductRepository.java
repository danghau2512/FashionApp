package com.example.fashionshop.repository;

import com.example.fashionshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByStatus(String status);

    List<Product> findByNameContainingIgnoreCaseAndStatus(String keyword, String status);

    List<Product> findByCategory_IdAndStatus(Long categoryId, String status);

    Long countByStatus(String status);

    @Query("""
        SELECT COALESCE(SUM(p.soldCount), 0)
        FROM Product p
    """)
    Long getTotalSoldQuantity();

    @Query("""
        SELECT p
        FROM Product p
        LEFT JOIN p.category c
        WHERE (:keyword IS NULL OR :keyword = ''
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:status IS NULL OR :status = '' OR p.status = :status)
        AND (:categoryId IS NULL OR c.id = :categoryId)
        ORDER BY p.id DESC
    """)
    List<Product> searchAdminProducts(@Param("keyword") String keyword,
                                      @Param("status") String status,
                                      @Param("categoryId") Long categoryId);
}