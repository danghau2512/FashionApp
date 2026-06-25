package com.example.fashionshopmobile.request;

import java.math.BigDecimal;

public class AdminProductRequest {

    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String imageUrl;
    private String brand;
    private String gender;
    private String status;

    public AdminProductRequest(Long categoryId,
                               String name,
                               String description,
                               BigDecimal price,
                               BigDecimal salePrice,
                               String imageUrl,
                               String brand,
                               String gender,
                               String status) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.gender = gender;
        this.status = status;
    }
}