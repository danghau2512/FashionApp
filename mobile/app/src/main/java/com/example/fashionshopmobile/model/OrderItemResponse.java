package com.example.fashionshopmobile.model;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private Long productId;
    private Long variantId;
    private String productName;
    private String size;
    private String color;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}