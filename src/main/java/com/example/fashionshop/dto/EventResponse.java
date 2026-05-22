package com.example.fashionshop.dto;

import java.time.LocalDateTime;

public class EventResponse {

    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String eventType;
    private Integer score;
    private LocalDateTime createdAt;

    public EventResponse(Long id, Long userId, Long productId,
                         String productName, String eventType,
                         Integer score, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.eventType = eventType;
        this.score = score;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getEventType() {
        return eventType;
    }

    public Integer getScore() {
        return score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}