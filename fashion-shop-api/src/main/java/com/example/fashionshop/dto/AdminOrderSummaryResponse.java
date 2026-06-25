package com.example.fashionshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminOrderSummaryResponse {

    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime createdAt;

    public AdminOrderSummaryResponse(Long id, Long userId, String receiverName, String receiverPhone,
                                     String deliveryAddress, BigDecimal totalAmount, String paymentMethod,
                                     String paymentStatus, String orderStatus, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getOrderStatus() { return orderStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
