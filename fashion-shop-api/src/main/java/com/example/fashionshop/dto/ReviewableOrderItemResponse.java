package com.example.fashionshop.dto;

import java.time.LocalDateTime;

public class ReviewableOrderItemResponse {

    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private String productName;
    private String size;
    private String color;
    private Integer quantity;
    private LocalDateTime orderCreatedAt;

    public ReviewableOrderItemResponse(Long orderItemId, Long orderId, Long productId,
                                       String productName, String size, String color,
                                       Integer quantity, LocalDateTime orderCreatedAt) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.orderCreatedAt = orderCreatedAt;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderCreatedAt() {
        return orderCreatedAt;
    }
}