package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.AdminOrderAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminOrderSummary;
import com.example.fashionshopmobile.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderListActivity extends AppCompatActivity {

    private TextView btnBack;
    private EditText edtSearch;
    private Spinner spnStatus;
    private RecyclerView rvOrders;
    private TextView txtMessage;

    private AdminOrderAdapter adapter;
    private SessionManager sessionManager;
    private String currentKeyword = "";
    private String currentStatus = "";

    private final String[] statusLabels = {"Tất cả", "Chờ xử lý", "Đang vận chuyển", "Hoàn thành", "Đã hủy"};
    private final String[] statusValues = {"", "PENDING", "SHIPPING", "COMPLETED", "CANCELLED"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền quản lý đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupEvents();
        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null && sessionManager.isAdmin()) {
            loadOrders();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);
        spnStatus = findViewById(R.id.spnStatus);
        rvOrders = findViewById(R.id.rvOrders);
        txtMessage = findViewById(R.id.txtMessage);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusLabels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(spinnerAdapter);
    }

    private void setupRecyclerView() {
        adapter = new AdminOrderAdapter(order -> {
            Intent intent = new Intent(AdminOrderListActivity.this, AdminOrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s == null ? "" : s.toString().trim();
                loadOrders();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStatus = statusValues[position];
                loadOrders();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadOrders() {
        txtMessage.setText("Đang tải danh sách đơn hàng...");
        String keyword = currentKeyword == null || currentKeyword.isEmpty() ? null : currentKeyword;
        String status = currentStatus == null || currentStatus.isEmpty() ? null : currentStatus;

        ApiClient.getApiService().getAdminOrders(keyword, status).enqueue(new Callback<List<AdminOrderSummary>>() {
            @Override
            public void onResponse(Call<List<AdminOrderSummary>> call, Response<List<AdminOrderSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setOrders(response.body());
                    txtMessage.setText(response.body().isEmpty() ? "Không có đơn hàng phù hợp" : "");
                } else {
                    txtMessage.setText("Không lấy được danh sách đơn hàng");
                    Toast.makeText(AdminOrderListActivity.this, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminOrderSummary>> call, Throwable t) {
                txtMessage.setText("Lỗi API đơn hàng: " + t.getMessage());
                Toast.makeText(AdminOrderListActivity.this, "Không kết nối được API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
