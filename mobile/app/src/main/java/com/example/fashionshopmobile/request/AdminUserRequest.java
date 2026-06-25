package com.example.fashionshopmobile.request;

public class AdminUserRequest {
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String role;
    private String status;

    public AdminUserRequest(String email, String fullName, String phone, String avatarUrl,
                            String role, String status) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.status = status;
    }
}
