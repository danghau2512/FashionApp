package com.example.fashionshop.repository;

import com.example.fashionshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminStatisticsRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = """
        SELECT COALESCE(SUM(o.total_amount), 0)
        FROM orders o
        WHERE o.order_status = 'COMPLETED'
          AND YEAR(o.created_at) = :year
    """, nativeQuery = true)
    BigDecimal getTotalRevenueByYear(@Param("year") int year);

    @Query(value = """
        SELECT
            MONTH(o.created_at),
            COALESCE(SUM(o.total_amount), 0)
        FROM orders o
        WHERE o.order_status = 'COMPLETED'
          AND YEAR(o.created_at) = :year
        GROUP BY MONTH(o.created_at)
        ORDER BY MONTH(o.created_at)
    """, nativeQuery = true)
    List<Object[]> getMonthlyRevenueByYear(@Param("year") int year);

    @Query(value = """
        SELECT
            p.id,
            p.name,
            c.name,
            p.price,
            p.sale_price,
            p.image_url,
            COALESCE(SUM(oi.quantity), 0)
        FROM products p
        LEFT JOIN categories c ON c.id = p.category_id
        JOIN order_items oi ON oi.product_id = p.id
        JOIN orders o ON o.id = oi.order_id
        WHERE o.order_status = 'COMPLETED'
          AND o.created_at >= :startTime
          AND o.created_at < :endTime
        GROUP BY p.id, p.name, c.name, p.price, p.sale_price, p.image_url
        ORDER BY COALESCE(SUM(oi.quantity), 0) DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> getBestSellers(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    @Query(value = """
        SELECT
            p.id,
            p.name,
            c.name,
            p.price,
            p.sale_price,
            p.image_url,
            0
        FROM products p
        LEFT JOIN categories c ON c.id = p.category_id
        WHERE NOT EXISTS (
            SELECT 1
            FROM order_items oi
            JOIN orders o ON o.id = oi.order_id
            WHERE oi.product_id = p.id
              AND o.order_status = 'COMPLETED'
              AND o.created_at >= :startTime
              AND o.created_at < :endTime
        )
        ORDER BY p.id DESC
    """, nativeQuery = true)
    List<Object[]> getNoSaleProducts(@Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);
}