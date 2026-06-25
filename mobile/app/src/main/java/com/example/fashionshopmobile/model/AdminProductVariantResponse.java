package com.example.fashionshopmobile.model;

public class AdminProductVariantResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String size;
    private String color;
    private Integer quantity;
    private String imageUrl;
    private String status;

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }
}