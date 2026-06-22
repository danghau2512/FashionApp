package com.example.fashionshopmobile.utils;

public class ImageUrlUtils {

    private static final String SERVER_URL = "http://10.0.2.2:8080";

    public static String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/")) {
            return SERVER_URL + imageUrl;
        }

        return SERVER_URL + "/" + imageUrl;
    }
}