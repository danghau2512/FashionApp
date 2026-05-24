package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class AddressResponse {

    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String addressDetail;
    private String ward;
    private String district;
    private String province;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean defaultAddress;

    public AddressResponse(Long id, Long userId, String receiverName, String receiverPhone,
                           String addressDetail, String ward, String district, String province,
                           BigDecimal latitude, BigDecimal longitude, Boolean defaultAddress) {
        this.id = id;
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.addressDetail = addressDetail;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultAddress = defaultAddress;
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