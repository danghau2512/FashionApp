package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GhnLeadtimeData {

    private Long leadtime;

    @JsonProperty("order_date")
    private Long orderDate;

    public GhnLeadtimeData() {
    }

    public Long getLeadtime() {
        return leadtime;
    }

    public Long getOrderDate() {
        return orderDate;
    }
}