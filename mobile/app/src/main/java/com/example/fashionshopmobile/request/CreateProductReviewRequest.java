package com.example.fashionshopmobile.request;

public class CreateProductReviewRequest {
    private Long userId;
    private Long productId;
    private Long orderItemId;
    private Integer rating;
    private String comment;

    public CreateProductReviewRequest(Long userId, Long productId, Long orderItemId, Integer rating, String comment) {
        this.userId = userId;
        this.productId = productId;
        this.orderItemId = orderItemId;
        this.rating = rating;
        this.comment = comment;
    }
}