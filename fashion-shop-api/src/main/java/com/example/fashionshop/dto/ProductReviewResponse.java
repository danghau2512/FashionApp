package com.example.fashionshop.dto;

import java.time.LocalDateTime;

public class ProductReviewResponse {

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
    private LocalDateTime createdAt;

    public ProductReviewResponse(Long id, Long userId, String userName, String userAvatarUrl,
                                 Long productId, Long orderItemId,
                                 String size, String color,
                                 Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
        this.productId = productId;
        this.orderItemId = orderItemId;
        this.size = size;
        this.color = color;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}