package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GhnLeadtimeRequest {

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

    public GhnLeadtimeRequest(Integer fromDistrictId,
                              String fromWardCode,
                              Integer toDistrictId,
                              String toWardCode,
                              Integer serviceId) {
        this.fromDistrictId = fromDistrictId;
        this.fromWardCode = fromWardCode;
        this.toDistrictId = toDistrictId;
        this.toWardCode = toWardCode;
        this.serviceId = serviceId;
    }
}