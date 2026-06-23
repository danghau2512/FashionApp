package com.example.fashionshopmobile.model;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {

    private Long id;
    private Long userId;

    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;

    private BigDecimal totalProductPrice;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String note;
    private String createdAt;

    private List<OrderItemResponse> items;

    public Long getId() {
        return id;
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

    public List<OrderItemResponse> getItems() {
        return items;
    }
}