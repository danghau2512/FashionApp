package com.example.fashionshopmobile.model;

public class ProductReview {

    private Long id;
    private Long userId;
    private String userName;
    private String userAvatarUrl;
    private Long productId;
    private Long orderItemId;

    private String size;
    private String color;

    private Integer rating;
    private String comment;
    private String createdAt;

    public ProductReview() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}