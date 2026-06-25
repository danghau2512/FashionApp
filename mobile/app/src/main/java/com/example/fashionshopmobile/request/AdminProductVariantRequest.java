package com.example.fashionshopmobile.request;

public class AdminProductVariantRequest {

    private String size;
    private String color;
    private Integer quantity;
    private String imageUrl;
    private String status;

    public AdminProductVariantRequest(String size,
                                      String color,
                                      Integer quantity,
                                      String imageUrl,
                                      String status) {
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.status = status;
    }
}