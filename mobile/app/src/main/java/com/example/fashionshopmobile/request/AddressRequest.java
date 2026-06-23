package com.example.fashionshopmobile.request;

public class AddressRequest {

    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String addressDetail;

    private String ward;
    private String district;
    private String province;

    private Integer provinceId;
    private Integer districtId;
    private String wardCode;

    private Double latitude;
    private Double longitude;

    private Boolean defaultAddress;

    public AddressRequest(Long userId, String receiverName, String receiverPhone, String addressDetail, String ward, String district, String province, Integer provinceId, Integer districtId, String wardCode, Double latitude, Double longitude, Boolean defaultAddress) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.addressDetail = addressDetail;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardCode = wardCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultAddress = defaultAddress;
    }
}