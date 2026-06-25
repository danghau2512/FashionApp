package com.example.fashionshop.repository;

import com.example.fashionshop.entity.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminOrderRepository extends JpaRepository<ShopOrder, Long> {

    @Query(value = """
        SELECT
            o.id AS id,
            o.user_id AS userId,
            o.receiver_name AS receiverName,
            o.receiver_phone AS receiverPhone,
            o.delivery_address AS deliveryAddress,
            o.total_product_price AS totalProductPrice,
            o.shipping_fee AS shippingFee,
            o.discount_amount AS discountAmount,
            o.total_amount AS totalAmount,
            o.payment_method AS paymentMethod,
            o.payment_status AS paymentStatus,
            o.order_status AS orderStatus,
            o.note AS note,
            o.created_at AS createdAt,
            o.confirmed_by_admin_id AS confirmedByAdminId,
            confirmed_admin.full_name AS confirmedByAdminName,
            o.confirmed_at AS confirmedAt,
            o.cancelled_by_admin_id AS cancelledByAdminId,
            cancelled_admin.full_name AS cancelledByAdminName,
            o.cancelled_at AS cancelledAt,
            o.cancel_reason AS cancelReason
        FROM orders o
        LEFT JOIN users confirmed_admin ON confirmed_admin.id = o.confirmed_by_admin_id
        LEFT JOIN users cancelled_admin ON cancelled_admin.id = o.cancelled_by_admin_id
        WHERE (:status IS NULL OR :status = '' OR o.order_status = :status)
          AND (
                :keyword IS NULL OR :keyword = ''
                OR CAST(o.id AS CHAR) LIKE CONCAT('%', :keyword, '%')
                OR LOWER(COALESCE(o.receiver_name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR COALESCE(o.receiver_phone, '') LIKE CONCAT('%', :keyword, '%')
          )
        ORDER BY o.created_at DESC
    """, nativeQuery = true)
    List<AdminOrderProjection> searchAdminOrders(@Param("keyword") String keyword,
                                                  @Param("status") String status);

    @Query(value = """
        SELECT
            o.id AS id,
            o.user_id AS userId,
            o.receiver_name AS receiverName,
            o.receiver_phone AS receiverPhone,
            o.delivery_address AS deliveryAddress,
            o.total_product_price AS totalProductPrice,
            o.shipping_fee AS shippingFee,
            o.discount_amount AS discountAmount,
            o.total_amount AS totalAmount,
            o.payment_method AS paymentMethod,
            o.payment_status AS paymentStatus,
            o.order_status AS orderStatus,
            o.note AS note,
            o.created_at AS createdAt,
            o.confirmed_by_admin_id AS confirmedByAdminId,
            confirmed_admin.full_name AS confirmedByAdminName,
            o.confirmed_at AS confirmedAt,
            o.cancelled_by_admin_id AS cancelledByAdminId,
            cancelled_admin.full_name AS cancelledByAdminName,
            o.cancelled_at AS cancelledAt,
            o.cancel_reason AS cancelReason
        FROM orders o
        LEFT JOIN users confirmed_admin ON confirmed_admin.id = o.confirmed_by_admin_id
        LEFT JOIN users cancelled_admin ON cancelled_admin.id = o.cancelled_by_admin_id
        WHERE o.id = :orderId
    """, nativeQuery = true)
    Optional<AdminOrderProjection> findAdminOrderDetail(@Param("orderId") Long orderId);

    @Modifying
    @Query(value = """
        UPDATE orders
        SET order_status = 'CONFIRMED',
            confirmed_by_admin_id = :adminId,
            confirmed_at = NOW(),
            updated_at = NOW()
        WHERE id = :orderId
    """, nativeQuery = true)
    int confirmOrder(@Param("orderId") Long orderId,
                     @Param("adminId") Long adminId);

    @Modifying
    @Query(value = """
        UPDATE orders
        SET order_status = 'CANCELLED',
            payment_status = CASE
                WHEN payment_status = 'PAID' THEN payment_status
                ELSE 'CANCELLED'
            END,
            cancelled_by_admin_id = :adminId,
            cancelled_at = NOW(),
            cancel_reason = :cancelReason,
            updated_at = NOW()
        WHERE id = :orderId
    """, nativeQuery = true)
    int cancelOrderByAdmin(@Param("orderId") Long orderId,
                           @Param("adminId") Long adminId,
                           @Param("cancelReason") String cancelReason);
}
