package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.AdminUserAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminUser;
import com.example.fashionshopmobile.request.AdminUserStatusRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserListActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView btnAddUser;
    private TextView txtMessage;
    private EditText edtSearch;
    private Spinner spnRole;
    private Spinner spnStatus;
    private RecyclerView rvUsers;

    private SessionManager sessionManager;
    private AdminUserAdapter adapter;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private final String[] roleLabels = {"Tất cả vai trò", "Khách hàng", "Quản trị viên"};
    private final String[] roleValues = {"", "CUSTOMER", "ADMIN"};
    private final String[] statusLabels = {"Tất cả trạng thái", "Đang hoạt động", "Đã khóa"};
    private final String[] statusValues = {"", "ACTIVE", "LOCKED"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền quản lý người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupFilters();
        setupEvents();
        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            loadUsers();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAddUser = findViewById(R.id.btnAddUser);
        txtMessage = findViewById(R.id.txtMessage);
        edtSearch = findViewById(R.id.edtSearch);
        spnRole = findViewById(R.id.spnRole);
        spnStatus = findViewById(R.id.spnStatus);
        rvUsers = findViewById(R.id.rvUsers);
    }

    private void setupRecyclerView() {
        adapter = new AdminUserAdapter(sessionManager.getUserId(), new AdminUserAdapter.UserActionListener() {
            @Override
            public void onView(AdminUser user) {
                Intent intent = new Intent(AdminUserListActivity.this, AdminUserDetailActivity.class);
                intent.putExtra("userId", user.getId());
                startActivity(intent);
            }

            @Override
            public void onEdit(AdminUser user) {
                Intent intent = new Intent(AdminUserListActivity.this, AdminUserFormActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("userId", user.getId());
                startActivity(intent);
            }

            @Override
            public void onToggleStatus(AdminUser user) {
                showToggleStatusDialog(user);
            }
        });

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }

    private void setupFilters() {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleLabels);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRole.setAdapter(roleAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusLabels);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminUserListActivity.this, AdminUserFormActivity.class);
            intent.putExtra("mode", "create");
            startActivity(intent);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scheduleSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spnRole.setOnItemSelectedListener(new SimpleItemSelectedListener(this::loadUsers));
        spnStatus.setOnItemSelectedListener(new SimpleItemSelectedListener(this::loadUsers));
    }

    private void scheduleSearch() {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        searchRunnable = this::loadUsers;
        searchHandler.postDelayed(searchRunnable, 250);
    }

    private void loadUsers() {
        String keyword = edtSearch.getText() == null ? "" : edtSearch.getText().toString().trim();
        String role = roleValues[spnRole.getSelectedItemPosition()];
        String status = statusValues[spnStatus.getSelectedItemPosition()];

        txtMessage.setText("Đang tải danh sách người dùng...");

        ApiClient.getApiService().getAdminUsers(keyword, role, status).enqueue(new Callback<List<AdminUser>>() {
            @Override
            public void onResponse(Call<List<AdminUser>> call, Response<List<AdminUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AdminUser> users = response.body();
                    adapter.setUsers(users);
                    txtMessage.setText("Tìm thấy " + users.size() + " người dùng");
                } else {
                    txtMessage.setText("Không lấy được danh sách người dùng");
                    Toast.makeText(AdminUserListActivity.this, "Lỗi tải danh sách người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminUser>> call, Throwable t) {
                txtMessage.setText("Lỗi API: " + t.getMessage());
            }
        });
    }

    private void showToggleStatusDialog(AdminUser user) {
        boolean locked = "LOCKED".equalsIgnoreCase(user.getStatus());
        String nextStatus = locked ? "ACTIVE" : "LOCKED";
        String action = locked ? "mở khóa" : "khóa";
        String name = user.getFullName() == null || user.getFullName().trim().isEmpty()
                ? user.getEmail()
                : user.getFullName();

        new AlertDialog.Builder(this)
                .setTitle(locked ? "Mở khóa tài khoản" : "Khóa tài khoản")
                .setMessage("Bạn có chắc muốn " + action + " tài khoản " + name + " không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xác nhận", (dialog, which) -> updateUserStatus(user.getId(), nextStatus))
                .show();
    }

    private void updateUserStatus(Long userId, String status) {
        Long adminId = sessionManager.getUserId();
        if (adminId == null) {
            Toast.makeText(this, "Không lấy được adminId", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getApiService()
                .updateAdminUserStatus(userId, adminId, new AdminUserStatusRequest(status))
                .enqueue(new Callback<AdminUser>() {
                    @Override
                    public void onResponse(Call<AdminUser> call, Response<AdminUser> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AdminUserListActivity.this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(AdminUserListActivity.this, "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminUser> call, Throwable t) {
                        Toast.makeText(AdminUserListActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
