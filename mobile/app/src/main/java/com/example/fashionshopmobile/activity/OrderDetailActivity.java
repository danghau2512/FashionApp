package com.example.fashionshopmobile.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.OrderDetailProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.OrderResponse;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvOrderCode, tvOrderStatus, tvCreatedAt;
    private TextView tvReceiverName, tvReceiverPhone, tvDeliveryAddress, tvPaymentInfo;
    private TextView tvTotalProductPrice, tvShippingFee, tvDiscountAmount, tvTotalAmount;
    private TextView tvEmptyOrderItems;
    private RecyclerView rvOrderDetailProducts;
    private Button btnCancelOrder;

    private OrderDetailProductAdapter productAdapter;
    private Long orderId;

    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getLongExtra("order_id", -1);

        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickEvents();
        loadOrderDetail();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);

        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvPaymentInfo = findViewById(R.id.tvPaymentInfo);

        tvTotalProductPrice = findViewById(R.id.tvTotalProductPrice);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        tvEmptyOrderItems = findViewById(R.id.tvEmptyOrderItems);
        rvOrderDetailProducts = findViewById(R.id.rvOrderDetailProducts);

        btnCancelOrder = findViewById(R.id.btnCancelOrder);
    }

    private void setupRecyclerView() {
        productAdapter = new OrderDetailProductAdapter();
        rvOrderDetailProducts.setLayoutManager(new LinearLayoutManager(this));
        rvOrderDetailProducts.setAdapter(productAdapter);
        rvOrderDetailProducts.setNestedScrollingEnabled(false);
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnCancelOrder.setOnClickListener(v -> showCancelConfirmDialog());
    }

    private void loadOrderDetail() {
        ApiClient.getApiService().getOrderById(orderId).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindOrderDetail(response.body());
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không lấy được chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi chi tiết đơn hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindOrderDetail(OrderResponse order) {
        tvOrderCode.setText("Đơn hàng #" + order.getId());
        tvOrderStatus.setText("Trạng thái: " + getStatusText(order.getOrderStatus()));
        tvCreatedAt.setText("Ngày đặt: " + formatDate(order.getCreatedAt()));

        tvReceiverName.setText("Người nhận: " + getTextOrDefault(order.getReceiverName(), "Chưa có"));
        tvReceiverPhone.setText("Số điện thoại: " + getTextOrDefault(order.getReceiverPhone(), "Chưa có"));
        tvDeliveryAddress.setText("Địa chỉ: " + getTextOrDefault(order.getDeliveryAddress(), "Chưa có"));
        tvPaymentInfo.setText("Thanh toán: " + getPaymentText(order.getPaymentMethod(), order.getPaymentStatus()));

        tvTotalProductPrice.setText("Tiền sản phẩm: " + formatPrice(order.getTotalProductPrice()));
        tvShippingFee.setText("Phí vận chuyển: " + formatPrice(order.getShippingFee()));
        tvDiscountAmount.setText("Giảm giá: " + formatPrice(order.getDiscountAmount()));
        tvTotalAmount.setText("Tổng tiền: " + formatPrice(order.getTotalAmount()));

        productAdapter.setData(order.getItems());
        updateCancelButton(order);

        if (order.getItems() == null || order.getItems().isEmpty()) {
            tvEmptyOrderItems.setVisibility(View.VISIBLE);
        } else {
            tvEmptyOrderItems.setVisibility(View.GONE);
        }
    }

    private void updateCancelButton(OrderResponse order) {
        boolean canCancel = "PENDING".equals(order.getOrderStatus())
                && !"PAID".equals(order.getPaymentStatus());

        if (canCancel) {
            btnCancelOrder.setVisibility(View.VISIBLE);
        } else {
            btnCancelOrder.setVisibility(View.GONE);
        }
    }

    private void showCancelConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này không?")
                .setNegativeButton("Không", null)
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelOrder())
                .show();
    }

    private void cancelOrder() {
        btnCancelOrder.setEnabled(false);

        ApiClient.getApiService().cancelOrder(orderId).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                btnCancelOrder.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(
                            OrderDetailActivity.this,
                            "Đã hủy đơn hàng",
                            Toast.LENGTH_SHORT
                    ).show();

                    bindOrderDetail(response.body());
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(
                            OrderDetailActivity.this,
                            "Không thể hủy đơn hàng. Chỉ hủy được đơn đang chờ xác nhận và chưa thanh toán",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                btnCancelOrder.setEnabled(true);

                Toast.makeText(
                        OrderDetailActivity.this,
                        "Lỗi hủy đơn hàng: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        return priceFormatter.format(price) + "đ";
    }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return "Chưa có";
        }

        return date.replace("T", " ");
    }

    private String getTextOrDefault(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    private String getPaymentText(String method, String status) {
        return getTextOrDefault(method, "Chưa có") + " - " + getPaymentStatusText(status);
    }

    private String getPaymentStatusText(String status) {
        if (status == null) {
            return "Chưa thanh toán";
        }

        if (status.equals("PAID")) {
            return "Đã thanh toán";
        }

        if (status.equals("UNPAID")) {
            return "Chưa thanh toán";
        }

        if (status.equals("FAILED")) {
            return "Thanh toán thất bại";
        }

        if (status.equals("CANCELLED")) {
            return "Đã hủy";
        }

        return status;
    }

    private String getStatusText(String status) {
        if (status == null) {
            return "Không rõ";
        }

        if (status.equals("PENDING")) {
            return "Chờ xác nhận";
        }

        if (status.equals("CONFIRMED") || status.equals("PROCESSING") || status.equals("PACKING")) {
            return "Chờ lấy hàng";
        }

        if (status.equals("SHIPPING") || status.equals("DELIVERING")) {
            return "Đang giao hàng";
        }

        if (status.equals("DELIVERED") || status.equals("COMPLETED")) {
            return "Đã giao hàng";
        }

        if (status.equals("CANCELLED")) {
            return "Đã hủy";
        }

        return status;
    }
}