package com.example.fashionshopmobile.model;

import java.math.BigDecimal;

public class AdminOrderSummary {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String createdAt;
    private Long confirmedByAdminId;
    private String confirmedByAdminName;
    private Long cancelledByAdminId;
    private String cancelledByAdminName;
    private String cancelReason;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getOrderStatus() { return orderStatus; }
    public String getCreatedAt() { return createdAt; }
    public Long getConfirmedByAdminId() { return confirmedByAdminId; }
    public String getConfirmedByAdminName() { return confirmedByAdminName; }
    public Long getCancelledByAdminId() { return cancelledByAdminId; }
    public String getCancelledByAdminName() { return cancelledByAdminName; }
    public String getCancelReason() { return cancelReason; }
}
