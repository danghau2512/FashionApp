package com.example.fashionshopmobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.fashionshopmobile.model.User;

public class SessionManager {

    private static final String PREF_NAME = "fashion_shop_session";

    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIREBASE_UID = "firebase_uid";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_AVATAR_URL = "avatar_url";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUser(User user) {
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_FIREBASE_UID, user.getFirebaseUid());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_AVATAR_URL, user.getAvatarUrl());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_STATUS, user.getStatus());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public Long getUserId() {
        long userId = sharedPreferences.getLong(KEY_USER_ID, -1);

        if (userId == -1) {
            return null;
        }

        return userId;
    }

    public String getFirebaseUid() {
        return sharedPreferences.getString(KEY_FIREBASE_UID, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, null);
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "CUSTOMER");
    }

    public String getStatus() {
        return sharedPreferences.getString(KEY_STATUS, "ACTIVE");
    }
    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    public String getAvatarUrl() {
        return sharedPreferences.getString(KEY_AVATAR_URL, null);
    }

    public boolean isAdmin() {
        String role = getRole();
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}