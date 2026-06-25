package com.example.fashionshopmobile.request;

public class AdminOrderActionRequest {
    private Long adminId;
    private String cancelReason;

    public AdminOrderActionRequest(Long adminId) {
        this.adminId = adminId;
    }

    public AdminOrderActionRequest(Long adminId, String cancelReason) {
        this.adminId = adminId;
        this.cancelReason = cancelReason;
    }

    public Long getAdminId() { return adminId; }
    public String getCancelReason() { return cancelReason; }
}
