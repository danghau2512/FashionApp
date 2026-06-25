package com.example.fashionshop.integration.vnpay.dto;

public class VnPayReturnResult {

    private Long orderId;
    private boolean success;
    private String message;

    public VnPayReturnResult(Long orderId, boolean success, String message) {
        this.orderId = orderId;
        this.success = success;
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}