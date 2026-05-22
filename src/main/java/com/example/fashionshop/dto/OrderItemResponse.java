package com.example.fashionshop.dto;

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

    public OrderItemResponse(Long id, Long productId, Long variantId,
                             String productName, String size, String color,
                             BigDecimal price, Integer quantity, BigDecimal subtotal) {
        this.id = id;
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getVariantId() {
        return variantId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}