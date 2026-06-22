package com.example.fashionshopmobile.model;

import java.math.BigDecimal;

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
    private Integer provinceId;
    private Integer districtId;
    private String wardCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

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
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();

        if (addressDetail != null && !addressDetail.trim().isEmpty()) {
            fullAddress.append(addressDetail);
        }

        if (ward != null && !ward.trim().isEmpty()) {
            fullAddress.append(", ").append(ward);
        }

        if (district != null && !district.trim().isEmpty()) {
            fullAddress.append(", ").append(district);
        }

        if (province != null && !province.trim().isEmpty()) {
            fullAddress.append(", ").append(province);
        }

        return fullAddress.toString();
    }
}