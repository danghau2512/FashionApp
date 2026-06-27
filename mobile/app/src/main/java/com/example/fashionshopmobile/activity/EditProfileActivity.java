package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.request.UpdateUserRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextView btnBack, btnAddAvatar;
    private ImageView imgAvatar;
    private EditText edtEmail, edtFullName, edtPhone;
    private Button btnSave;

    private SessionManager sessionManager;
    private Long userId;
    private String currentAvatarUrl;


    private ActivityResultLauncher<String> avatarPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            finish();
            return;
        }

        initViews();
        setupAvatarPicker();
        setupClickEvents();
        loadUserInfo();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
        btnAddAvatar = findViewById(R.id.btnAddAvatar);
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> updateProfile());
        btnAddAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        imgAvatar.setOnClickListener(v -> openAvatarPreview());
    }

    private void loadUserInfo() {
        ApiClient.getApiService().getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    edtEmail.setText(user.getEmail());
                    edtFullName.setText(user.getFullName());
                    edtPhone.setText(user.getPhone());

                    currentAvatarUrl = user.getAvatarUrl();
                    loadAvatar(currentAvatarUrl);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không lấy được thông tin user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProfile() {
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (fullName.isEmpty()) {
            edtFullName.setError("Họ tên không được để trống");
            edtFullName.requestFocus();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^[0-9]{9,11}$")) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            edtPhone.requestFocus();
            return;
        }

        UpdateUserRequest request = new UpdateUserRequest(
                fullName,
                phone,
                currentAvatarUrl
        );

        btnSave.setEnabled(false);

        ApiClient.getApiService().updateUser(userId, request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    User updatedUser = response.body();
                    sessionManager.saveUser(updatedUser);

                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Lỗi cập nhật: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void loadAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            imgAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            return;
        }

        Glide.with(this)
                .load(buildImageUrl(avatarUrl))
                .circleCrop()
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(imgAvatar);
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

    private void setupAvatarPicker() {
        avatarPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgAvatar.setImageURI(uri);
                        uploadAvatar(uri);
                    }
                }
        );
    }

    private void uploadAvatar(Uri uri) {
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MultipartBody.Part imagePart = createImagePart(uri);

            btnAddAvatar.setEnabled(false);
            Toast.makeText(this, "Đang upload ảnh đại diện...", Toast.LENGTH_SHORT).show();

            ApiClient.getApiService()
                    .uploadUserAvatar(userId, imagePart)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            btnAddAvatar.setEnabled(true);

                            if (response.isSuccessful() && response.body() != null) {
                                User updatedUser = response.body();

                                currentAvatarUrl = updatedUser.getAvatarUrl();
                                sessionManager.saveUser(updatedUser);
                                loadAvatar(currentAvatarUrl);

                                Toast.makeText(EditProfileActivity.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMessage = "Upload thất bại. Code: " + response.code();

                                try {
                                    if (response.errorBody() != null) {
                                        errorMessage += "\n" + response.errorBody().string();
                                    }
                                } catch (Exception e) {
                                    errorMessage += "\nKhông đọc được lỗi server";
                                }

                                Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            btnAddAvatar.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this, "Lỗi upload ảnh: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            btnAddAvatar.setEnabled(true);
            Toast.makeText(this, "Không đọc được ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private MultipartBody.Part createImagePart(Uri uri) throws IOException {
        String mimeType = getContentResolver().getType(uri);

        if (mimeType == null) {
            mimeType = "image/jpeg";
        }

        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

        if (extension == null) {
            extension = "jpg";
        }

        File tempFile = new File(
                getCacheDir(),
                "avatar_" + System.currentTimeMillis() + "." + extension
        );

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            if (inputStream == null) {
                throw new IOException("Không mở được file ảnh");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), tempFile);
        return MultipartBody.Part.createFormData("file", tempFile.getName(), requestBody);
    }
    private void openAvatarPreview() {
        if (currentAvatarUrl == null || currentAvatarUrl.isEmpty()) {
            Toast.makeText(this, "Chưa có ảnh đại diện để xem", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(EditProfileActivity.this, AvatarPreviewActivity.class);
        intent.putExtra("avatarUrl", currentAvatarUrl);
        startActivity(intent);
    }
}