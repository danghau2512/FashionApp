package com.example.fashionshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ShippingQuoteRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<Long> cartItemIds;

    @NotNull(message = "District ID không được để trống")
    private Integer districtId;

    @NotBlank(message = "Ward code không được để trống")
    private String wardCode;

    public ShippingQuoteRequest() {
    }

    public ShippingQuoteRequest(Long userId,
                                List<Long> cartItemIds,
                                Integer districtId,
                                String wardCode) {
        this.userId = userId;
        this.cartItemIds = cartItemIds;
        this.districtId = districtId;
        this.wardCode = wardCode;
    }

    public Long getUserId() {
        return userId;
    }

    public List<Long> getCartItemIds() {
        return cartItemIds;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public String getWardCode() {
        return wardCode;
    }
}