package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;

public class AvatarPreviewActivity extends AppCompatActivity {

    private ImageView imgPreviewAvatar;
    private TextView btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_preview);

        imgPreviewAvatar = findViewById(R.id.imgPreviewAvatar);
        btnClose = findViewById(R.id.btnClose);

        String avatarUrl = getIntent().getStringExtra("avatarUrl");

        if (avatarUrl == null || avatarUrl.isEmpty()) {
            Toast.makeText(this, "Chưa có ảnh đại diện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Glide.with(this)
                .load(buildImageUrl(avatarUrl))
                .fitCenter()
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(imgPreviewAvatar);

        btnClose.setOnClickListener(v -> finish());
    }

    private String buildImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }

        if (imageUrl.startsWith("http")) {
            return imageUrl;
        }

        String baseUrl = ApiClient.getBaseUrl();

        if (baseUrl.endsWith("/") && imageUrl.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + imageUrl;
        }

        if (!baseUrl.endsWith("/") && !imageUrl.startsWith("/")) {
            return baseUrl + "/" + imageUrl;
        }

        return baseUrl + imageUrl;
    }
}