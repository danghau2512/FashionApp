package com.example.fashionshopmobile.model.shipping;

import java.math.BigDecimal;

public class ShippingQuote {

    private BigDecimal shippingFee;
    private String estimatedDelivery;
    private Integer serviceId;
    private String serviceName;

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