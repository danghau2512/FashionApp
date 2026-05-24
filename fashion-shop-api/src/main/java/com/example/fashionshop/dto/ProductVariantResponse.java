package com.example.fashionshop.dto;

public class ProductVariantResponse {

    private Long id;
    private Long productId;
    private String size;
    private String color;
    private Integer quantity;
    private String imageUrl;

    public ProductVariantResponse(Long id, Long productId, String size,
                                  String color, Integer quantity, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

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