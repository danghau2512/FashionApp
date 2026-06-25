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
    private Long confirmedByAdminId;
    private String confirmedByAdminName;
    private Long cancelledByAdminId;
    private String cancelledByAdminName;
    private String cancelReason;

    public AdminOrderSummaryResponse(Long id, Long userId, String receiverName, String receiverPhone,
                                     String deliveryAddress, BigDecimal totalAmount, String paymentMethod,
                                     String paymentStatus, String orderStatus, LocalDateTime createdAt,
                                     Long confirmedByAdminId, String confirmedByAdminName,
                                     Long cancelledByAdminId, String cancelledByAdminName,
                                     String cancelReason) {
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
        this.confirmedByAdminId = confirmedByAdminId;
        this.confirmedByAdminName = confirmedByAdminName;
        this.cancelledByAdminId = cancelledByAdminId;
        this.cancelledByAdminName = cancelledByAdminName;
        this.cancelReason = cancelReason;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getConfirmedByAdminId() {
        return confirmedByAdminId;
    }

    public String getConfirmedByAdminName() {
        return confirmedByAdminName;
    }

    public Long getCancelledByAdminId() {
        return cancelledByAdminId;
    }

    public String getCancelledByAdminName() {
        return cancelledByAdminName;
    }

    public String getCancelReason() {
        return cancelReason;
    }
}
