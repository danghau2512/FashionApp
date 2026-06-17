package com.example.fashionshopmobile.model;

public class StoreLocation {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String status;

    public StoreLocation() {
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }
}