package com.example.fashionshop.dto;

public class AdminUserRequest {

    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String role;
    private String status;

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}
