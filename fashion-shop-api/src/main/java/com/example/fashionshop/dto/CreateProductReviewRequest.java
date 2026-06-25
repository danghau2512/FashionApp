package com.example.fashionshop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateProductReviewRequest {

    @NotNull(message = "userId không được để trống")
    private Long userId;

    @NotNull(message = "productId không được để trống")
    private Long productId;

    @NotNull(message = "orderItemId không được để trống")
    private Long orderItemId;

    @NotNull(message = "rating không được để trống")
    @Min(value = 1, message = "Số sao tối thiểu là 1")
    @Max(value = 5, message = "Số sao tối đa là 5")
    private Integer rating;

    private String comment;

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}