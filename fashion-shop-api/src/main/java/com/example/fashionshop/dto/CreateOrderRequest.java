package com.example.fashionshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotEmpty(message = "Danh sách sản phẩm thanh toán không được để trống")
    private List<Long> cartItemIds;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String deliveryAddress;
    @NotNull(message = "District ID giao hàng không được để trống")
    private Integer deliveryDistrictId;

    @NotBlank(message = "Ward code giao hàng không được để trống")
    private String deliveryWardCode;

    private BigDecimal deliveryLatitude;
    private BigDecimal deliveryLongitude;

    private BigDecimal shippingFee;

    private String paymentMethod;

    private String note;
    public Integer getDeliveryDistrictId() {
        return deliveryDistrictId;
    }

    public String getDeliveryWardCode() {
        return deliveryWardCode;
    }
    public Long getUserId() {
        return userId;
    }

    public List<Long> getCartItemIds() {
        return cartItemIds;
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

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getNote() {
        return note;
    }
}