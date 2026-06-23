package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GhnWardRequest {

    @JsonProperty("district_id")
    private Integer districtId;

    public GhnWardRequest(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getDistrictId() {
        return districtId;
    }
}