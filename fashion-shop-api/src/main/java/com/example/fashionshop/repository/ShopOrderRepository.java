package com.example.fashionshop.repository;

import com.example.fashionshop.entity.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {

    List<ShopOrder> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Long countByOrderStatus(String orderStatus);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM ShopOrder o
        WHERE o.orderStatus <> 'CANCELLED'
    """)
    BigDecimal getTotalRevenue();
}