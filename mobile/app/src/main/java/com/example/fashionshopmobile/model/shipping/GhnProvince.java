package com.example.fashionshopmobile.model.shipping;

public class GhnProvince {

    private Integer provinceId;
    private String provinceName;
    private Integer status;

    public Integer getProvinceId() {
        return provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return provinceName;
    }
}