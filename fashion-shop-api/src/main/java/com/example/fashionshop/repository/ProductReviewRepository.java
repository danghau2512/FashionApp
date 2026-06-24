package com.example.fashionshop.repository;

import com.example.fashionshop.entity.OrderItem;
import com.example.fashionshop.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProduct_IdAndStatusOrderByCreatedAtDesc(Long productId, String status);

    boolean existsByOrderItem_Id(Long orderItemId);

    Long countByProduct_IdAndStatus(Long productId, String status);

    @Query("""
        SELECT COALESCE(AVG(r.rating), 0)
        FROM ProductReview r
        WHERE r.product.id = :productId
          AND r.status = 'ACTIVE'
    """)
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    @Query("""
        SELECT oi
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.user.id = :userId
          AND oi.product.id = :productId
          AND o.orderStatus IN ('COMPLETED', 'DELIVERED')
          AND NOT EXISTS (
              SELECT r.id
              FROM ProductReview r
              WHERE r.orderItem.id = oi.id
          )
        ORDER BY o.createdAt ASC, oi.id ASC
    """)
    List<OrderItem> findReviewableOrderItems(@Param("userId") Long userId,
                                             @Param("productId") Long productId);
}