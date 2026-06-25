package com.example.fashionshopmobile.model;

import java.math.BigDecimal;
import java.util.List;

public class AdminOrderDetail {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private BigDecimal totalProductPrice;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String note;
    private String createdAt;
    private Long confirmedByAdminId;
    private String confirmedByAdminName;
    private String confirmedAt;
    private Long cancelledByAdminId;
    private String cancelledByAdminName;
    private String cancelledAt;
    private String cancelReason;
    private List<AdminOrderItem> items;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public BigDecimal getTotalProductPrice() { return totalProductPrice; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getOrderStatus() { return orderStatus; }
    public String getNote() { return note; }
    public String getCreatedAt() { return createdAt; }
    public Long getConfirmedByAdminId() { return confirmedByAdminId; }
    public String getConfirmedByAdminName() { return confirmedByAdminName; }
    public String getConfirmedAt() { return confirmedAt; }
    public Long getCancelledByAdminId() { return cancelledByAdminId; }
    public String getCancelledByAdminName() { return cancelledByAdminName; }
    public String getCancelledAt() { return cancelledAt; }
    public String getCancelReason() { return cancelReason; }
    public List<AdminOrderItem> getItems() { return items; }
}
