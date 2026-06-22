package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminDashboard;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView txtAdminHello;
    private TextView btnAdminLogout;

    private TextView txtTotalRevenue;
    private TextView txtTotalOrders;
    private TextView txtPendingOrders;
    private TextView txtTotalProducts;
    private TextView txtAdminMessage;
    private TextView btnAdminStatistics;
    private TextView btnAdminOrders;
    private TextView btnAdminProducts;
    private TextView btnAdminUsers;

    private SessionManager sessionManager;

    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);

        initViews();

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền vào trang quản trị", Toast.LENGTH_SHORT).show();
            openLogin();
            return;
        }

        setupClickEvents();
        showAdminName();
        loadDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager != null && sessionManager.isAdmin() && txtAdminMessage != null) {
            loadDashboard();
        }
    }

    private void initViews() {
        txtAdminHello = findViewById(R.id.txtAdminHello);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);
        txtTotalOrders = findViewById(R.id.txtTotalOrders);
        txtPendingOrders = findViewById(R.id.txtPendingOrders);
        txtTotalProducts = findViewById(R.id.txtTotalProducts);
        txtAdminMessage = findViewById(R.id.txtAdminMessage);
        btnAdminStatistics = findViewById(R.id.btnAdminStatistics);
        btnAdminOrders = findViewById(R.id.btnAdminOrders);
        btnAdminProducts = findViewById(R.id.btnAdminProducts);
        btnAdminUsers = findViewById(R.id.btnAdminUsers);
    }

    private void setupClickEvents() {
        btnAdminLogout.setOnClickListener(v -> logout());

        btnAdminStatistics.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng thống kê sẽ làm ở bước sau", Toast.LENGTH_SHORT).show();
        });
        btnAdminOrders.setOnClickListener(v -> {
            Toast.makeText(this, "Quản lý đơn hàng sẽ làm ở bước sau", Toast.LENGTH_SHORT).show();
        });

        btnAdminProducts.setOnClickListener(v -> {
            Toast.makeText(this, "Quản lý sản phẩm sẽ làm ở bước sau", Toast.LENGTH_SHORT).show();
        });

        btnAdminUsers.setOnClickListener(v -> {
            Toast.makeText(this, "Quản lý người dùng sẽ làm ở bước sau", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAdminName() {
        String fullName = sessionManager.getFullName();

        if (fullName != null && !fullName.trim().isEmpty()) {
            txtAdminHello.setText("Xin chào, " + fullName);
        } else {
            txtAdminHello.setText("Xin chào Admin");
        }
    }

    private void loadDashboard() {
        ApiClient.getApiService().getAdminDashboard().enqueue(new Callback<AdminDashboard>() {
            @Override
            public void onResponse(Call<AdminDashboard> call, Response<AdminDashboard> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showDashboard(response.body());
                    txtAdminMessage.setText("");
                } else {
                    txtAdminMessage.setText("Không lấy được dữ liệu tổng quan");
                }
            }

            @Override
            public void onFailure(Call<AdminDashboard> call, Throwable t) {
                txtAdminMessage.setText("Lỗi API tổng quan: " + t.getMessage());
            }
        });
    }

    private void showDashboard(AdminDashboard dashboard) {
        txtTotalRevenue.setText(formatPrice(dashboard.getTotalRevenue()));
        txtTotalOrders.setText(String.valueOf(getLongValue(dashboard.getTotalOrders())));
        txtPendingOrders.setText(String.valueOf(getLongValue(dashboard.getPendingOrders())));
        txtTotalProducts.setText(String.valueOf(getLongValue(dashboard.getTotalProducts())));
    }

    private long getLongValue(Long value) {
        return value != null ? value : 0;
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        return priceFormatter.format(price) + "đ";
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logout();
        openLogin();
    }

    private void openLogin() {
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}