package com.example.fashionshopmobile.request;

public class CreateEventRequest {

    private Long userId;
    private Long productId;
    private String eventType;

    public CreateEventRequest(Long userId, Long productId, String eventType) {
        this.userId = userId;
        this.productId = productId;
        this.eventType = eventType;
    }
}