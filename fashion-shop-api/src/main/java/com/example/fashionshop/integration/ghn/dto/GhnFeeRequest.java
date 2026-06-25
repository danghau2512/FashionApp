package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GhnFeeRequest {

    @JsonProperty("from_district_id")
    private Integer fromDistrictId;

    @JsonProperty("from_ward_code")
    private String fromWardCode;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    @JsonProperty("service_id")
    private Integer serviceId;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("length")
    private Integer length;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("insurance_value")
    private Integer insuranceValue;

    public GhnFeeRequest(Integer fromDistrictId,
                         String fromWardCode,
                         Integer toDistrictId,
                         String toWardCode,
                         Integer serviceId,
                         Integer weight,
                         Integer length,
                         Integer width,
                         Integer height) {
        this.fromDistrictId = fromDistrictId;
        this.fromWardCode = fromWardCode;
        this.toDistrictId = toDistrictId;
        this.toWardCode = toWardCode;
        this.serviceId = serviceId;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.insuranceValue = 0;
    }

    public Integer getFromDistrictId() {
        return fromDistrictId;
    }

    public String getFromWardCode() {
        return fromWardCode;
    }

    public Integer getToDistrictId() {
        return toDistrictId;
    }

    public String getToWardCode() {
        return toWardCode;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getInsuranceValue() {
        return insuranceValue;
    }
}