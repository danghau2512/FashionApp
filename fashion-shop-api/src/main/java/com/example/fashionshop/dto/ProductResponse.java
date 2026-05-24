package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class ProductResponse {

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
    private Integer viewCount;
    private Integer soldCount;

    public ProductResponse(Long id, Long categoryId, String categoryName, String name,
                           String description, BigDecimal price, BigDecimal salePrice,
                           String imageUrl, String brand, String gender,
                           Integer viewCount, Integer soldCount) {
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

    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getSoldCount() {
        return soldCount;
    }
}