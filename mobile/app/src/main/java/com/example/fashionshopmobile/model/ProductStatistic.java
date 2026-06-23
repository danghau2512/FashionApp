package com.example.fashionshopmobile.model;

import java.math.BigDecimal;

public class ProductStatistic {

    private Long productId;
    private String productName;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String imageUrl;
    private Long soldQuantity;

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getSoldQuantity() {
        return soldQuantity;
    }
}