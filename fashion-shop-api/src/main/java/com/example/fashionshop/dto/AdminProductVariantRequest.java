package com.example.fashionshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class AdminProductVariantRequest {

    private Long id;

    private String size;

    private String color;

    @Min(value = 0, message = "Số lượng không được âm")
    private Integer quantity;

    private String imageUrl;

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Trạng thái biến thể chỉ được là ACTIVE hoặc INACTIVE")
    private String status;

    public AdminProductVariantRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}