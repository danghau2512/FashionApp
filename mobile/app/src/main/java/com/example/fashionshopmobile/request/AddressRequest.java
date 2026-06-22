package com.example.fashionshopmobile.request;

public class AddressRequest {
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String addressDetail;
    private String ward;
    private String district;
    private String province;
    private Boolean defaultAddress;

    public AddressRequest(Long userId, String receiverName, String receiverPhone,
                          String addressDetail, String ward, String district,
                          String province, Boolean defaultAddress) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.addressDetail = addressDetail;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.defaultAddress = defaultAddress;
    }
}