package com.example.fashionshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminOrderDetailResponse {

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
    private LocalDateTime createdAt;
    private List<AdminOrderItemResponse> items;

    public AdminOrderDetailResponse(Long id, Long userId, String receiverName, String receiverPhone,
                                    String deliveryAddress, BigDecimal totalProductPrice,
                                    BigDecimal shippingFee, BigDecimal discountAmount,
                                    BigDecimal totalAmount, String paymentMethod, String paymentStatus,
                                    String orderStatus, String note, LocalDateTime createdAt,
                                    List<AdminOrderItemResponse> items) {
        this.id = id;
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveryAddress = deliveryAddress;
        this.totalProductPrice = totalProductPrice;
        this.shippingFee = shippingFee;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.note = note;
        this.createdAt = createdAt;
        this.items = items;
    }

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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<AdminOrderItemResponse> getItems() { return items; }
}
