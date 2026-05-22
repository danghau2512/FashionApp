package com.example.fashionshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateEventRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Product ID không được để trống")
    private Long productId;

    @NotBlank(message = "Event type không được để trống")
    private String eventType;

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getEventType() {
        return eventType;
    }
}