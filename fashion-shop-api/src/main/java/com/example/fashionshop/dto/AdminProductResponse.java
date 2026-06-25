package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class AdminProductResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String imageUrl;
    private String brand;
    private String gender;
    private String status;
    private Integer viewCount;
    private Integer soldCount;

    public AdminProductResponse(Long id, Long categoryId, String categoryName,
                                String name, String description,
                                BigDecimal price, BigDecimal salePrice,
                                String imageUrl, String brand, String gender,
                                String status, Integer viewCount, Integer soldCount) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.gender = gender;
        this.status = status;
        this.viewCount = viewCount;
        this.soldCount = soldCount;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public String getBrand() {
        return brand;
    }

    public String getGender() {
        return gender;
    }

    public String getStatus() {
        return status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getSoldCount() {
        return soldCount;
    }
}