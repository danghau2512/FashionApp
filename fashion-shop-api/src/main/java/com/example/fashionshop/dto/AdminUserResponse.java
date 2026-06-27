package com.example.fashionshop.dto;

import com.example.fashionshop.entity.User;

import java.time.format.DateTimeFormatter;

public class AdminUserResponse {

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

    public AdminUserResponse(Long id, String firebaseUid, String email, String fullName,
                             String phone, String avatarUrl, String role, String status,
                             String createdAt, String updatedAt) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AdminUserResponse fromEntity(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new AdminUserResponse(
                user.getId(),
                user.getFirebaseUid(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt() == null ? null : user.getCreatedAt().format(formatter),
                user.getUpdatedAt() == null ? null : user.getUpdatedAt().format(formatter)
        );
    }

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
