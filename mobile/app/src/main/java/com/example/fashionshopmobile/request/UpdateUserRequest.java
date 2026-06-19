package com.example.fashionshopmobile.request;

public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String avatarUrl;

    public UpdateUserRequest(String fullName, String phone, String avatarUrl) {
        this.fullName = fullName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
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
}