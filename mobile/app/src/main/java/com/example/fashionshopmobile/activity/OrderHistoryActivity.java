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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickEvents();
        loadOrderHistory();
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
                    List<OrderSummary> orders = response.body();

                    orderHistoryAdapter.setData(orders);
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
}