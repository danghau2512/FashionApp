package com.example.fashionshopmobile.model;

public class AdminUser {
    private Long id;
    private String firebaseUid;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String role;
    private String status;
    private String createdAt;
    private String updatedAt;

    public Long getId() {
        return id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
