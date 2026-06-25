package com.example.fashionshopmobile.model;

import java.math.BigDecimal;

public class OrderSummary {
    private Long id;
    private Long userId;
    private String receiverName;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String createdAt;

    private String productImageUrl;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public String getProductImageUrl() {
        return productImageUrl;
    }
}