package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class StoreLocationResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public StoreLocationResponse(Long id, String name, String address,
                                 String phone, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }
}