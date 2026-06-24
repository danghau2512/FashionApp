package com.example.fashionshop.dto;

import jakarta.validation.constraints.NotNull;

public class AdminOrderActionRequest {

    @NotNull(message = "Admin ID không được để trống")
    private Long adminId;

    private String cancelReason;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
