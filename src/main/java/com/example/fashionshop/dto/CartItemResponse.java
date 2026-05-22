package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long id;
    private Long userId;
    private Long productId;
    private Long variantId;

    private String productName;
    private String productImageUrl;
    private String size;
    private String color;

    private BigDecimal price;
    private Integer quantity;
    private Integer stockQuantity;
    private BigDecimal subtotal;

    public CartItemResponse(Long id, Long userId, Long productId, Long variantId,
                            String productName, String productImageUrl,
                            String size, String color,
                            BigDecimal price, Integer quantity,
                            Integer stockQuantity, BigDecimal subtotal) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.stockQuantity = stockQuantity;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public String getProductImageUrl() {
        return productImageUrl;
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}