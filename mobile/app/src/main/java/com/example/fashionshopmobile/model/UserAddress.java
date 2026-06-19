package com.example.fashionshopmobile.model;

public class UserAddress {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String addressDetail;
    private String ward;
    private String district;
    private String province;
    private Boolean defaultAddress;

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

    public Boolean getDefaultAddress() {
        return defaultAddress;
    }
}