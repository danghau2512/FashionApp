package com.example.fashionshopmobile.model;

public class ProductVariant {

    private Long id;
    private Long productId;
    private String size;
    private String color;
    private Integer quantity;
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
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

    public String getImageUrl() {
        return imageUrl;
    }
}