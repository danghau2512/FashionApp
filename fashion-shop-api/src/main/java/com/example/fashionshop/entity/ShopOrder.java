package com.example.fashionshop.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "delivery_latitude")
    private BigDecimal deliveryLatitude;

    @Column(name = "delivery_longitude")
    private BigDecimal deliveryLongitude;

    @Column(name = "total_product_price")
    private BigDecimal totalProductPrice;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "order_status")
    private String orderStatus;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ShopOrder() {
    }

    @PrePersist
    public void prePersist() {
        if (paymentMethod == null) {
            paymentMethod = "COD";
        }

        if (paymentStatus == null) {
            paymentStatus = "UNPAID";
        }

        if (orderStatus == null) {
            orderStatus = "PENDING";
        }

        if (shippingFee == null) {
            shippingFee = BigDecimal.ZERO;
        }

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
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

    public BigDecimal getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public BigDecimal getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public BigDecimal getTotalProductPrice() {
        return totalProductPrice;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
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

    public String getNote() {
        return note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setDeliveryLatitude(BigDecimal deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public void setDeliveryLongitude(BigDecimal deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public void setTotalProductPrice(BigDecimal totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setNote(String note) {
        this.note = note;
    }
}