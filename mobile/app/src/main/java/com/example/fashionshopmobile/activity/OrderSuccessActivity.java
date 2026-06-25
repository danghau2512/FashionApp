package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.MainActivity;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.OrderResponse;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView tvOrderCode;
    private TextView tvSuccessTotal;
    private MaterialButton btnContinueShopping;

    private final NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        initViews();
        handleOrderData();

        btnContinueShopping.setOnClickListener(view -> openMainActivity());
    }

    private void initViews() {
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvSuccessTotal = findViewById(R.id.tvSuccessTotal);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void handleOrderData() {
        Uri deepLink = getIntent().getData();

        if (deepLink != null && "order-success".equals(deepLink.getHost())) {
            handleVnPayDeepLink(deepLink);
        } else {
            handleCodResult();
        }
    }

    private void handleCodResult() {
        long orderId = getIntent().getLongExtra("order_id", -1);
        String totalText = getIntent().getStringExtra("total_amount");

        if (orderId <= 0) {
            showErrorAndReturnHome("Không tìm thấy mã đơn hàng");
            return;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (totalText != null && !totalText.isEmpty()) {
            try {
                totalAmount = new BigDecimal(totalText);
            } catch (NumberFormatException ignored) {
                totalAmount = BigDecimal.ZERO;
            }
        }

        showOrderSuccess(orderId, totalAmount);
    }

    private void handleVnPayDeepLink(Uri deepLink) {
        String orderIdText = deepLink.getQueryParameter("orderId");

        if (orderIdText == null || orderIdText.isEmpty()) {
            showErrorAndReturnHome("Không nhận được mã đơn hàng");
            return;
        }

        try {
            Long orderId = Long.parseLong(orderIdText);
            loadVnPayOrder(orderId);
        } catch (NumberFormatException exception) {
            showErrorAndReturnHome("Mã đơn hàng không hợp lệ");
        }
    }

    private void loadVnPayOrder(Long orderId) {
        tvOrderCode.setText("Đang kiểm tra đơn hàng...");
        tvSuccessTotal.setText("");

        ApiClient.getApiService().getOrderById(orderId).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showErrorAndReturnHome("Không lấy được thông tin đơn hàng");
                    return;
                }

                OrderResponse order = response.body();

                if (!"VNPAY".equals(order.getPaymentMethod())) {
                    showErrorAndReturnHome("Phương thức thanh toán không hợp lệ");
                    return;
                }

                if (!"PAID".equals(order.getPaymentStatus())) {
                    showErrorAndReturnHome("Đơn hàng chưa được thanh toán thành công");
                    return;
                }

                showOrderSuccess(order.getId(), order.getTotalAmount());
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable throwable) {
                showErrorAndReturnHome("Không kết nối được đến máy chủ");
            }
        });
    }

    private void showOrderSuccess(Long orderId, BigDecimal totalAmount) {
        BigDecimal safeTotal = totalAmount == null ? BigDecimal.ZERO : totalAmount;

        tvOrderCode.setText("Mã đơn hàng: DH" + orderId);
        tvSuccessTotal.setText("Tổng thanh toán: " + formatter.format(safeTotal) + "đ");
    }

    private void showErrorAndReturnHome(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        openMainActivity();
    }

    private void openMainActivity() {
        Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}