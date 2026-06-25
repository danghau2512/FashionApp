package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GhnAvailableService {

    @JsonProperty("service_id")
    private Integer serviceId;

    @JsonProperty("short_name")
    private String shortName;

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    public GhnAvailableService() {
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public String getShortName() {
        return shortName;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }
}