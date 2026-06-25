package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GhnFeeData {

    private BigDecimal total;

    public GhnFeeData() {
    }

    public BigDecimal getTotal() {
        return total;
    }
}