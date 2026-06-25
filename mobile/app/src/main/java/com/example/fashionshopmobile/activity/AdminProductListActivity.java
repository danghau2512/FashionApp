package com.example.fashionshopmobile.activity;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.AdminProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminProductResponse;
import com.example.fashionshopmobile.request.UpdateProductStatusRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;

public class AdminProductListActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView btnAddProduct;
    private EditText edtSearch;
    private Spinner spinnerStatus;
    private TextView txtMessage;
    private RecyclerView rvProducts;

    private AdminProductAdapter adapter;
    private SessionManager sessionManager;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private boolean firstSpinnerLoad = true;

    private final String[] statusLabels = {"Tất cả", "Đang bán", "Đã ẩn"};
    private final String[] statusValues = {"", "ACTIVE", "INACTIVE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_list);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền vào quản lý sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupStatusSpinner();
        setupEvents();

        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager != null && sessionManager.isAdmin() && adapter != null) {
            loadProducts();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackAdminProducts);
        btnAddProduct = findViewById(R.id.btnAddAdminProduct);
        edtSearch = findViewById(R.id.edtAdminProductSearch);
        spinnerStatus = findViewById(R.id.spinnerAdminProductStatus);
        txtMessage = findViewById(R.id.txtAdminProductMessage);
        rvProducts = findViewById(R.id.rvAdminProducts);
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter(new AdminProductAdapter.OnAdminProductActionListener() {
            @Override
            public void onDetail(AdminProductResponse product) {
                Intent intent = new Intent(AdminProductListActivity.this, AdminProductVariantListActivity.class);
                intent.putExtra("productId", product.getId());
                intent.putExtra("productName", product.getName());
                startActivity(intent);
            }

            @Override
            public void onEdit(AdminProductResponse product) {
                Intent intent = new Intent(AdminProductListActivity.this, AdminProductFormActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }

            @Override
            public void onToggleStatus(AdminProductResponse product) {
                confirmToggleStatus(product);
            }
        });

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);
    }

    private void setupStatusSpinner() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusLabels
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProductListActivity.this, AdminProductFormActivity.class);
            startActivity(intent);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> loadProducts();
                searchHandler.postDelayed(searchRunnable, 450);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       android.view.View view,
                                       int position,
                                       long id) {
                if (firstSpinnerLoad) {
                    firstSpinnerLoad = false;
                    return;
                }

                loadProducts();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void loadProducts() {
        String keyword = edtSearch.getText() != null
                ? edtSearch.getText().toString().trim()
                : "";

        String status = statusValues[spinnerStatus.getSelectedItemPosition()];

        txtMessage.setText("Đang tải sản phẩm...");

        ApiClient.getApiService()
                .getAdminProducts(keyword, status, null)
                .enqueue(new Callback<List<AdminProductResponse>>() {
                    @Override
                    public void onResponse(Call<List<AdminProductResponse>> call,
                                           Response<List<AdminProductResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<AdminProductResponse> products = response.body();
                            adapter.setData(products);

                            if (products.isEmpty()) {
                                txtMessage.setText("Không có sản phẩm phù hợp");
                            } else {
                                txtMessage.setText("Tổng: " + products.size() + " sản phẩm");
                            }
                        } else {
                            txtMessage.setText("Không tải được danh sách sản phẩm");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AdminProductResponse>> call, Throwable t) {
                        txtMessage.setText("Lỗi API sản phẩm: " + t.getMessage());
                    }
                });
    }

    private void confirmToggleStatus(AdminProductResponse product) {
        boolean active = "ACTIVE".equalsIgnoreCase(product.getStatus());
        String newStatus = active ? "INACTIVE" : "ACTIVE";
        String actionText = active ? "ẩn" : "hiện";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn " + actionText + " sản phẩm này không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Đồng ý", (dialog, which) -> updateProductStatus(product, newStatus))
                .show();
    }

    private void updateProductStatus(AdminProductResponse product, String newStatus) {
        Long adminId = sessionManager.getUserId();

        if (adminId == null) {
            Toast.makeText(this, "Không tìm thấy adminId, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getApiService()
                .updateAdminProductStatus(
                        product.getId(),
                        adminId,
                        new UpdateProductStatusRequest(newStatus)
                )
                .enqueue(new Callback<AdminProductResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductResponse> call,
                                           Response<AdminProductResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminProductListActivity.this,
                                    "Cập nhật trạng thái thành công",
                                    Toast.LENGTH_SHORT).show();
                            loadProducts();
                        } else {
                            Toast.makeText(AdminProductListActivity.this,
                                    "Cập nhật thất bại. Kiểm tra quyền admin hoặc dữ liệu API",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductResponse> call, Throwable t) {
                        Toast.makeText(AdminProductListActivity.this,
                                "Lỗi API: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}