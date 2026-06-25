package com.example.fashionshopmobile.request;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequest {

    private Long userId;
    private List<Long> cartItemIds;
    private String receiverName;
    private String receiverPhone;
    private String deliveryAddress;
    private BigDecimal deliveryLatitude;
    private BigDecimal deliveryLongitude;
    private Integer deliveryDistrictId;
    private String deliveryWardCode;
    private BigDecimal shippingFee;
    private String paymentMethod;
    private String note;


    public CreateOrderRequest(Long userId,
                              List<Long> cartItemIds,
                              String receiverName,
                              String receiverPhone,
                              String deliveryAddress,
                              BigDecimal deliveryLatitude,
                              BigDecimal deliveryLongitude,
                              Integer deliveryDistrictId,
                              String deliveryWardCode,
                              BigDecimal shippingFee,
                              String paymentMethod,
                              String note) {
        this.userId = userId;
        this.cartItemIds = cartItemIds;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveryAddress = deliveryAddress;
        this.deliveryLatitude = deliveryLatitude;
        this.deliveryLongitude = deliveryLongitude;
        this.deliveryDistrictId = deliveryDistrictId;
        this.deliveryWardCode = deliveryWardCode;
        this.shippingFee = shippingFee;
        this.paymentMethod = paymentMethod;
        this.note = note;
    }
}