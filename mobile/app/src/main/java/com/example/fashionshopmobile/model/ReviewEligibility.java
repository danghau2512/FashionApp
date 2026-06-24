package com.example.fashionshopmobile.model;

import java.util.List;

public class ReviewEligibility {
    private boolean canReview;
    private int availableReviewCount;
    private Double averageRating;
    private Long reviewCount;
    private List<ReviewableOrderItem> reviewableOrderItems;

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

    public List<ReviewableOrderItem> getReviewableOrderItems() {
        return reviewableOrderItems;
    }
}