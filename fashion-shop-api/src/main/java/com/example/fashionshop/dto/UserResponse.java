package com.example.fashionshop.dto;

import com.example.fashionshop.entity.User;

public class UserResponse {

    private Long id;
    private String firebaseUid;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String role;
    private String status;

    public UserResponse(Long id, String firebaseUid, String email, String fullName,
                        String phone, String avatarUrl, String role, String status) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.status = status;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirebaseUid(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus()
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
}