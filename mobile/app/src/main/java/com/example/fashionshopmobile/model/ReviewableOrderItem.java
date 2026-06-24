package com.example.fashionshopmobile.model;

public class ReviewableOrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private String productName;
    private String size;
    private String color;
    private Integer quantity;
    private String orderCreatedAt;

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

    public String getOrderCreatedAt() {
        return orderCreatedAt;
    }
}