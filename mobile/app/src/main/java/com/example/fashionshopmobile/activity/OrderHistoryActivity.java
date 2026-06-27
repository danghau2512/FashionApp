package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.OrderHistoryAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.OrderSummary;
import com.example.fashionshopmobile.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private TextView btnBack, tvEmptyOrders;
    private RecyclerView rvOrderHistory;

    private SessionManager sessionManager;
    private OrderHistoryAdapter orderHistoryAdapter;
    private Long userId;
    private String filterStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        filterStatus = getIntent().getStringExtra("filterStatus");

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        rvOrderHistory = findViewById(R.id.rvOrderHistory);
    }

    private void setupRecyclerView() {
        orderHistoryAdapter = new OrderHistoryAdapter();
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rvOrderHistory.setAdapter(orderHistoryAdapter);
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrderHistory() {
        ApiClient.getApiService().getOrdersByUserId(userId).enqueue(new Callback<List<OrderSummary>>() {
            @Override
            public void onResponse(Call<List<OrderSummary>> call, Response<List<OrderSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderSummary> orders = filterOrders(response.body());

                    orderHistoryAdapter.setData(orders);

                    if ("REVIEW".equals(filterStatus)) {
                        tvEmptyOrders.setText("Bạn không có đơn hàng nào chờ đánh giá");
                    } else {
                        tvEmptyOrders.setText("Bạn chưa có đơn hàng nào");
                    }

                    showEmpty(orders.isEmpty());
                } else {
                    orderHistoryAdapter.setData(new ArrayList<>());
                    showEmpty(true);
                    Toast.makeText(OrderHistoryActivity.this, "Không lấy được lịch sử mua hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderSummary>> call, Throwable t) {
                orderHistoryAdapter.setData(new ArrayList<>());
                showEmpty(true);
                Toast.makeText(OrderHistoryActivity.this, "Lỗi lịch sử mua hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showEmpty(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
        } else {
            tvEmptyOrders.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userId != null) {
            loadOrderHistory();
        }
    }

    private List<OrderSummary> filterOrders(List<OrderSummary> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }

        if (!"REVIEW".equals(filterStatus)) {
            return orders;
        }

        List<OrderSummary> filteredOrders = new ArrayList<>();

        for (OrderSummary order : orders) {
            String status = order.getOrderStatus();

            if (status == null) {
                continue;
            }

            if (status.equals("COMPLETED")) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }
}