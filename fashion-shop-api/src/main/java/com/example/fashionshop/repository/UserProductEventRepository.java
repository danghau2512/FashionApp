package com.example.fashionshop.repository;

import com.example.fashionshop.entity.UserProductEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fashionshop.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserProductEventRepository extends JpaRepository<UserProductEvent, Long> {

    List<UserProductEvent> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<UserProductEvent> findByProduct_IdOrderByCreatedAtDesc(Long productId);
    @Query("""
        SELECT event.product
        FROM UserProductEvent event
        WHERE event.product.status = 'ACTIVE'
        GROUP BY event.product
        ORDER BY SUM(event.score) DESC
        """)
    List<Product> findPopularProducts(Pageable pageable);
}