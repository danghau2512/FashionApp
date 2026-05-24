package com.example.fashionshopmobile.model;

import java.math.BigDecimal;

public class Product {

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