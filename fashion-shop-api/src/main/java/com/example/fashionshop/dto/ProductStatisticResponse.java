package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class ProductStatisticResponse {

    private Long productId;
    private String productName;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String imageUrl;
    private Long soldQuantity;

    public ProductStatisticResponse(Long productId,
                                    String productName,
                                    String categoryName,
                                    BigDecimal price,
                                    BigDecimal salePrice,
                                    String imageUrl,
                                    Long soldQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.price = price;
        this.salePrice = salePrice;
        this.imageUrl = imageUrl;
        this.soldQuantity = soldQuantity;
    }

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