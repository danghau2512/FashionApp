package com.example.fashionshop.integration.vnpay.dto;

public class VnPayPaymentResponse {

    private Long orderId;
    private String paymentUrl;
    private String paymentStatus;

    public VnPayPaymentResponse(Long orderId, String paymentUrl, String paymentStatus) {
        this.orderId = orderId;
        this.paymentUrl = paymentUrl;
        this.paymentStatus = paymentStatus;
    }

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