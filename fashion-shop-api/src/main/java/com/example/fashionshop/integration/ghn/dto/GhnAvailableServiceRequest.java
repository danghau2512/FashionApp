package com.example.fashionshop.integration.ghn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GhnAvailableServiceRequest {

    @JsonProperty("shop_id")
    private Integer shopId;

    @JsonProperty("from_district")
    private Integer fromDistrict;

    @JsonProperty("to_district")
    private Integer toDistrict;

    public GhnAvailableServiceRequest(Integer shopId,
                                      Integer fromDistrict,
                                      Integer toDistrict) {
        this.shopId = shopId;
        this.fromDistrict = fromDistrict;
        this.toDistrict = toDistrict;
    }
}