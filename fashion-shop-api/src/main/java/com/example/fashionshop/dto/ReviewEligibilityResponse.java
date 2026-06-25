package com.example.fashionshop.dto;

import java.util.List;

public class ReviewEligibilityResponse {

    private boolean canReview;
    private int availableReviewCount;
    private Double averageRating;
    private Long reviewCount;
    private List<ReviewableOrderItemResponse> reviewableOrderItems;

    public ReviewEligibilityResponse(boolean canReview, int availableReviewCount,
                                     Double averageRating, Long reviewCount,
                                     List<ReviewableOrderItemResponse> reviewableOrderItems) {
        this.canReview = canReview;
        this.availableReviewCount = availableReviewCount;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.reviewableOrderItems = reviewableOrderItems;
    }

    public boolean isCanReview() {
        return canReview;
    }

    public int getAvailableReviewCount() {
        return availableReviewCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

    public List<ReviewableOrderItemResponse> getReviewableOrderItems() {
        return reviewableOrderItems;
    }
}