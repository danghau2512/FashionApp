package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminUser;
import com.example.fashionshopmobile.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserDetailActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView txtMessage;
    private TextView txtUserId;
    private TextView txtFullName;
    private TextView txtEmail;
    private TextView txtPhone;
    private TextView txtRole;
    private TextView txtStatus;
    private TextView txtFirebaseUid;
    private TextView txtCreatedAt;
    private TextView txtUpdatedAt;

    private Long userId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền xem thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = getIntent().getLongExtra("userId", -1L);
        if (userId == -1L) {
            Toast.makeText(this, "Thiếu mã người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        btnBack.setOnClickListener(v -> finish());
        loadUserDetail();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtMessage = findViewById(R.id.txtMessage);
        txtUserId = findViewById(R.id.txtUserId);
        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        txtStatus = findViewById(R.id.txtStatus);
        txtFirebaseUid = findViewById(R.id.txtFirebaseUid);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtUpdatedAt = findViewById(R.id.txtUpdatedAt);
    }

    private void loadUserDetail() {
        txtMessage.setText("Đang tải thông tin người dùng...");

        ApiClient.getApiService().getAdminUserById(userId).enqueue(new Callback<AdminUser>() {
            @Override
            public void onResponse(Call<AdminUser> call, Response<AdminUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showUser(response.body());
                    txtMessage.setText("");
                } else {
                    txtMessage.setText("Không lấy được thông tin người dùng");
                }
            }

            @Override
            public void onFailure(Call<AdminUser> call, Throwable t) {
                txtMessage.setText("Lỗi API: " + t.getMessage());
            }
        });
    }

    private void showUser(AdminUser user) {
        txtUserId.setText("ID: #" + user.getId());
        txtFullName.setText("Họ tên: " + safe(user.getFullName()));
        txtEmail.setText("Email: " + safe(user.getEmail()));
        txtPhone.setText("Số điện thoại: " + safe(user.getPhone()));
        txtRole.setText("Vai trò: " + getRoleLabel(user.getRole()));
        txtStatus.setText("Trạng thái: " + getStatusLabel(user.getStatus()));
        txtFirebaseUid.setText("Firebase UID: " + safe(user.getFirebaseUid()));
        txtCreatedAt.setText("Ngày tạo: " + safe(user.getCreatedAt()));
        txtUpdatedAt.setText("Cập nhật: " + safe(user.getUpdatedAt()));
    }

    private String safe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Chưa có";
        }
        return value;
    }

    private String getRoleLabel(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "Quản trị viên";
        }
        return "Khách hàng";
    }

    private String getStatusLabel(String status) {
        if ("LOCKED".equalsIgnoreCase(status)) {
            return "Đã khóa";
        }
        return "Đang hoạt động";
    }
}
