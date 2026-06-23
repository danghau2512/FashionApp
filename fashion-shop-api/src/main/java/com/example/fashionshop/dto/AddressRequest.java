package com.example.fashionshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AddressRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String addressDetail;

    @NotBlank(message = "Phường/xã không được để trống")
    private String ward;

    @NotBlank(message = "Quận/huyện không được để trống")
    private String district;

    @NotBlank(message = "Tỉnh/thành phố không được để trống")
    private String province;

    @NotNull(message = "Mã tỉnh/thành phố không được để trống")
    private Integer provinceId;

    @NotNull(message = "Mã quận/huyện không được để trống")
    private Integer districtId;

    @NotBlank(message = "Mã phường/xã không được để trống")
    private String wardCode;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private Boolean defaultAddress;

    public Long getUserId() {
        return userId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public String getWard() {
        return ward;
    }

    public String getDistrict() {
        return district;
    }

    public String getProvince() {
        return province;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public String getWardCode() {
        return wardCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public Boolean getDefaultAddress() {
        return defaultAddress;
    }
}