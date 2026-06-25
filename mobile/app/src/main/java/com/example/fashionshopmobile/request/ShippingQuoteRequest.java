package com.example.fashionshopmobile.request;

import java.util.List;

public class ShippingQuoteRequest {

    private Long userId;
    private List<Long> cartItemIds;
    private Integer districtId;
    private String wardCode;

    public ShippingQuoteRequest(Long userId,
                                List<Long> cartItemIds,
                                Integer districtId,
                                String wardCode) {
        this.userId = userId;
        this.cartItemIds = cartItemIds;
        this.districtId = districtId;
        this.wardCode = wardCode;
    }
}