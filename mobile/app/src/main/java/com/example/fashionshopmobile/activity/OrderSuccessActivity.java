package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.MainActivity;
import com.example.fashionshopmobile.R;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        TextView tvOrderCode = findViewById(R.id.tvOrderCode);
        TextView tvSuccessTotal = findViewById(R.id.tvSuccessTotal);
        MaterialButton btnContinueShopping = findViewById(R.id.btnContinueShopping);

        long orderId = getIntent().getLongExtra("order_id", -1);
        String totalText = getIntent().getStringExtra("total_amount");

        BigDecimal total = BigDecimal.ZERO;

        if (totalText != null && !totalText.isEmpty()) {
            try {
                total = new BigDecimal(totalText);
            } catch (NumberFormatException ignored) {
                total = BigDecimal.ZERO;
            }
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        tvOrderCode.setText("Mã đơn hàng: DH" + orderId);
        tvSuccessTotal.setText("Tổng thanh toán: " + formatter.format(total) + "đ");

        btnContinueShopping.setOnClickListener(view -> {
            Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}