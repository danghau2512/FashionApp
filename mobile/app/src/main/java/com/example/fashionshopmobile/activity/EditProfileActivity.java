package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.request.UpdateUserRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextView btnBack;
    private ImageView imgAvatar;
    private EditText edtEmail, edtFullName, edtPhone;
    private Button btnSave;

    private SessionManager sessionManager;
    private Long userId;
    private String currentAvatarUrl;

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
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> updateProfile());
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
}