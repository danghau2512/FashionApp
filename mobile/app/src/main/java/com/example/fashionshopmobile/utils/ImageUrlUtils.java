package com.example.fashionshopmobile.utils;

import com.example.fashionshopmobile.api.ApiClient;

public class ImageUrlUtils {

    public static String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        imageUrl = imageUrl.trim();

        String baseUrl = ApiClient.getBaseUrl();
        String serverUrl = removeLastSlash(baseUrl);

        // Nếu database lỡ lưu localhost thì đổi sang IP hiện tại trong ApiClient
        if (imageUrl.startsWith("http://localhost:8080")) {
            return imageUrl.replace("http://localhost:8080", serverUrl);
        }

        // Nếu database lỡ lưu 10.0.2.2 thì đổi sang IP hiện tại trong ApiClient
        if (imageUrl.startsWith("http://10.0.2.2:8080")) {
            return imageUrl.replace("http://10.0.2.2:8080", serverUrl);
        }

        // Nếu backend đã trả link đầy đủ thì giữ nguyên
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        // Nếu backend trả /images/products/abc.jpg
        if (imageUrl.startsWith("/")) {
            return serverUrl + imageUrl;
        }

        // Nếu backend trả images/products/abc.jpg
        return serverUrl + "/" + imageUrl;
    }

    private static String removeLastSlash(String url) {
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }

        return url;
    }
}