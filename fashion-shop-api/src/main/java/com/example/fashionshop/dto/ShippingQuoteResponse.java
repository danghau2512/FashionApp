package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class ShippingQuoteResponse {

    private BigDecimal shippingFee;
    private String estimatedDelivery;
    private Integer serviceId;
    private String serviceName;

    public ShippingQuoteResponse(BigDecimal shippingFee,
                                 String estimatedDelivery,
                                 Integer serviceId,
                                 String serviceName) {
        this.shippingFee = shippingFee;
        this.estimatedDelivery = estimatedDelivery;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }
}