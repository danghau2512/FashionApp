package com.example.fashionshopmobile.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminUser;
import com.example.fashionshopmobile.request.AdminUserRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserFormActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView txtTitle;
    private TextView txtMessage;
    private EditText edtFullName;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtAvatarUrl;
    private Spinner spnRole;
    private Spinner spnStatus;
    private Button btnSave;
    private Button btnCancel;

    private SessionManager sessionManager;
    private String mode;
    private Long userId;
    private AdminUser editingUser;

    private final String[] roleLabels = {"Khách hàng", "Quản trị viên"};
    private final String[] roleValues = {"CUSTOMER", "ADMIN"};
    private final String[] statusLabels = {"Đang hoạt động", "Đã khóa"};
    private final String[] statusValues = {"ACTIVE", "LOCKED"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_form);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền quản lý người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = "create";
        }
        userId = getIntent().getLongExtra("userId", -1L);

        initViews();
        setupSpinners();
        setupEvents();

        if ("edit".equals(mode)) {
            txtTitle.setText("Sửa người dùng");
            btnSave.setText("Cập nhật");
            loadUserDetail();
        } else {
            txtTitle.setText("Thêm người dùng");
            btnSave.setText("Thêm");
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtTitle = findViewById(R.id.txtTitle);
        txtMessage = findViewById(R.id.txtMessage);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAvatarUrl = findViewById(R.id.edtAvatarUrl);
        spnRole = findViewById(R.id.spnRole);
        spnStatus = findViewById(R.id.spnStatus);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupSpinners() {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleLabels);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRole.setAdapter(roleAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusLabels);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> confirmSave());
    }

    private void loadUserDetail() {
        if (userId == null || userId == -1L) {
            Toast.makeText(this, "Thiếu mã người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtMessage.setText("Đang tải thông tin người dùng...");
        ApiClient.getApiService().getAdminUserById(userId).enqueue(new Callback<AdminUser>() {
            @Override
            public void onResponse(Call<AdminUser> call, Response<AdminUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    editingUser = response.body();
                    fillForm(editingUser);
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

    private void fillForm(AdminUser user) {
        edtFullName.setText(user.getFullName());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(user.getPhone());
        edtAvatarUrl.setText(user.getAvatarUrl());
        spnRole.setSelection("ADMIN".equalsIgnoreCase(user.getRole()) ? 1 : 0);
        spnStatus.setSelection("LOCKED".equalsIgnoreCase(user.getStatus()) ? 1 : 0);
    }

    private void confirmSave() {
        if (!validateForm()) {
            return;
        }

        String message = "edit".equals(mode)
                ? "Bạn có chắc muốn cập nhật người dùng này không?"
                : "Bạn có chắc muốn thêm người dùng này không?";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage(message)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xác nhận", (dialog, which) -> saveUser())
                .show();
    }

    private boolean validateForm() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            edtEmail.setError("Email không hợp lệ");
            return false;
        }

        return true;
    }

    private void saveUser() {
        Long adminId = sessionManager.getUserId();
        if (adminId == null) {
            Toast.makeText(this, "Không lấy được adminId", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminUserRequest request = new AdminUserRequest(
                edtEmail.getText().toString().trim(),
                edtFullName.getText().toString().trim(),
                edtPhone.getText().toString().trim(),
                edtAvatarUrl.getText().toString().trim(),
                roleValues[spnRole.getSelectedItemPosition()],
                statusValues[spnStatus.getSelectedItemPosition()]
        );

        Call<AdminUser> call;
        if ("edit".equals(mode)) {
            call = ApiClient.getApiService().updateAdminUser(userId, adminId, request);
        } else {
            call = ApiClient.getApiService().createAdminUser(adminId, request);
        }

        call.enqueue(new Callback<AdminUser>() {
            @Override
            public void onResponse(Call<AdminUser> call, Response<AdminUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String success = "edit".equals(mode) ? "Cập nhật người dùng thành công" : "Thêm người dùng thành công";
                    Toast.makeText(AdminUserFormActivity.this, success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminUserFormActivity.this, "Không thể lưu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminUser> call, Throwable t) {
                Toast.makeText(AdminUserFormActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
