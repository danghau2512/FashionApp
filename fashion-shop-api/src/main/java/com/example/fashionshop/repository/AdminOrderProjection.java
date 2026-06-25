package com.example.fashionshop.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AdminOrderProjection {
    Long getId();
    Long getUserId();
    String getReceiverName();
    String getReceiverPhone();
    String getDeliveryAddress();
    BigDecimal getTotalProductPrice();
    BigDecimal getShippingFee();
    BigDecimal getDiscountAmount();
    BigDecimal getTotalAmount();
    String getPaymentMethod();
    String getPaymentStatus();
    String getOrderStatus();
    String getNote();
    LocalDateTime getCreatedAt();
}
