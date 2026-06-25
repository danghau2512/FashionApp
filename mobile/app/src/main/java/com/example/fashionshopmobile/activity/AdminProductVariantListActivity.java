package com.example.fashionshopmobile.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.AdminProductVariantAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminProductVariantResponse;
import com.example.fashionshopmobile.request.UpdateProductVariantStatusRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductVariantListActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView btnAddVariant;
    private TextView txtProductName;
    private TextView txtMessage;
    private RecyclerView rvVariants;

    private AdminProductVariantAdapter adapter;
    private SessionManager sessionManager;

    private Long productId;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_variant_list);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền quản lý biến thể", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productId = getIntent().getLongExtra("productId", -1);
        productName = getIntent().getStringExtra("productName");

        if (productId == null || productId <= 0) {
            Toast.makeText(this, "Thiếu productId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupEvents();

        txtProductName.setText(productName != null ? productName : "ID sản phẩm: " + productId);

        loadVariants();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null && productId != null && productId > 0) {
            loadVariants();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackAdminVariants);
        btnAddVariant = findViewById(R.id.btnAddAdminVariant);
        txtProductName = findViewById(R.id.txtAdminVariantProductName);
        txtMessage = findViewById(R.id.txtAdminVariantMessage);
        rvVariants = findViewById(R.id.rvAdminVariants);
    }

    private void setupRecyclerView() {
        adapter = new AdminProductVariantAdapter(new AdminProductVariantAdapter.OnAdminVariantActionListener() {
            @Override
            public void onEdit(AdminProductVariantResponse variant) {
                Intent intent = new Intent(AdminProductVariantListActivity.this, AdminProductVariantFormActivity.class);
                intent.putExtra("variantId", variant.getId());
                startActivity(intent);
            }

            @Override
            public void onToggleStatus(AdminProductVariantResponse variant) {
                confirmToggleStatus(variant);
            }
        });

        rvVariants.setLayoutManager(new LinearLayoutManager(this));
        rvVariants.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnAddVariant.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProductVariantListActivity.this, AdminProductVariantFormActivity.class);
            intent.putExtra("productId", productId);
            intent.putExtra("productName", productName);
            startActivity(intent);
        });
    }

    private void loadVariants() {
        txtMessage.setText("Đang tải biến thể...");

        ApiClient.getApiService()
                .getAdminProductVariants(productId)
                .enqueue(new Callback<List<AdminProductVariantResponse>>() {
                    @Override
                    public void onResponse(Call<List<AdminProductVariantResponse>> call,
                                           Response<List<AdminProductVariantResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<AdminProductVariantResponse> variants = response.body();
                            adapter.setData(variants);

                            if (variants.isEmpty()) {
                                txtMessage.setText("Sản phẩm này chưa có biến thể");
                            } else {
                                txtMessage.setText("Tổng: " + variants.size() + " biến thể");
                            }
                        } else {
                            txtMessage.setText("Không tải được danh sách biến thể");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AdminProductVariantResponse>> call, Throwable t) {
                        txtMessage.setText("Lỗi API biến thể: " + t.getMessage());
                    }
                });
    }

    private void confirmToggleStatus(AdminProductVariantResponse variant) {
        boolean active = "ACTIVE".equalsIgnoreCase(variant.getStatus());
        String newStatus = active ? "INACTIVE" : "ACTIVE";
        String actionText = active ? "ẩn" : "hiện";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn " + actionText + " biến thể này không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Đồng ý", (dialog, which) -> updateVariantStatus(variant, newStatus))
                .show();
    }

    private void updateVariantStatus(AdminProductVariantResponse variant, String newStatus) {
        Long adminId = sessionManager.getUserId();

        if (adminId == null) {
            Toast.makeText(this, "Không tìm thấy adminId, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getApiService()
                .updateAdminProductVariantStatus(
                        variant.getId(),
                        adminId,
                        new UpdateProductVariantStatusRequest(newStatus)
                )
                .enqueue(new Callback<AdminProductVariantResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductVariantResponse> call,
                                           Response<AdminProductVariantResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminProductVariantListActivity.this,
                                    "Cập nhật trạng thái biến thể thành công",
                                    Toast.LENGTH_SHORT).show();
                            loadVariants();
                        } else {
                            Toast.makeText(AdminProductVariantListActivity.this,
                                    "Cập nhật thất bại. Kiểm tra quyền admin hoặc API",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductVariantResponse> call, Throwable t) {
                        Toast.makeText(AdminProductVariantListActivity.this,
                                "Lỗi API: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}