package com.example.fashionshopmobile.model;

public class VnPayPaymentResponse {

    private Long orderId;
    private String paymentUrl;
    private String paymentStatus;

    public Long getOrderId() {
        return orderId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}