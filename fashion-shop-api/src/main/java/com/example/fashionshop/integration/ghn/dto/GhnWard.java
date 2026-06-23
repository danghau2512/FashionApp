package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GhnWard {

    @JsonAlias("WardCode")
    private String wardCode;

    @JsonAlias("DistrictID")
    private Integer districtId;

    @JsonAlias("WardName")
    private String wardName;

    @JsonAlias("Status")
    private Integer status;

    public GhnWard() {
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}