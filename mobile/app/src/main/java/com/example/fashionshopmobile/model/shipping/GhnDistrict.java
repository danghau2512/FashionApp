package com.example.fashionshopmobile.model.shipping;

public class GhnDistrict {

    private Integer districtId;
    private Integer provinceId;
    private String districtName;
    private Integer status;

    public Integer getDistrictId() {
        return districtId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return districtName;
    }
}