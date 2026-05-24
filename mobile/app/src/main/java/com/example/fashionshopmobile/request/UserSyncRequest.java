package com.example.fashionshopmobile.request;

public class UserSyncRequest {
    private String firebaseUid;
    private String email;
    private String fullName;
    private String avatarUrl;

    public UserSyncRequest(String firebaseUid, String email, String fullName, String avatarUrl) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }
}