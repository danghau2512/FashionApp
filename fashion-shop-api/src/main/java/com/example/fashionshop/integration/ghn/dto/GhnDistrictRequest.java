package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GhnDistrictRequest {

    @JsonProperty("province_id")
    private Integer provinceId;

    public GhnDistrictRequest(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }
}