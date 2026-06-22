package com.example.fashionshopmobile.model.shipping;

public class GhnWard {

    private String wardCode;
    private Integer districtId;
    private String wardName;
    private Integer status;

    public String getWardCode() {
        return wardCode;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public String getWardName() {
        return wardName;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return wardName;
    }
}