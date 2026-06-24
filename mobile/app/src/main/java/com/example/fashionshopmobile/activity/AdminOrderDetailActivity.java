package com.example.fashionshopmobile.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.AdminOrderItemAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminOrderDetail;
import com.example.fashionshopmobile.request.AdminOrderActionRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView btnBack, txtDetailMessage, txtOrderCode, txtReceiver, txtPhone, txtAddress, txtCreatedAt;
    private TextView txtProductTotal, txtShippingFee, txtDiscount, txtTotalAmount, txtPayment, txtOrderStatus, txtNote;
    private TextView txtConfirmedInfo, txtCancelledInfo, txtCancelReason;
    private Button btnConfirmOrder, btnCancelOrder;
    private RecyclerView rvOrderItems;

    private AdminOrderItemAdapter itemAdapter;
    private SessionManager sessionManager;
    private AdminOrderDetail currentOrder;
    private Long orderId;
    private final NumberFormat moneyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền quản lý đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupEvents();
        loadOrderDetail();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtDetailMessage = findViewById(R.id.txtDetailMessage);
        txtOrderCode = findViewById(R.id.txtOrderCode);
        txtReceiver = findViewById(R.id.txtReceiver);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtProductTotal = findViewById(R.id.txtProductTotal);
        txtShippingFee = findViewById(R.id.txtShippingFee);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtPayment = findViewById(R.id.txtPayment);
        txtOrderStatus = findViewById(R.id.txtOrderStatus);
        txtNote = findViewById(R.id.txtNote);
        txtConfirmedInfo = findViewById(R.id.txtConfirmedInfo);
        txtCancelledInfo = findViewById(R.id.txtCancelledInfo);
        txtCancelReason = findViewById(R.id.txtCancelReason);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        rvOrderItems = findViewById(R.id.rvOrderItems);
    }

    private void setupRecyclerView() {
        itemAdapter = new AdminOrderItemAdapter();
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(itemAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirmOrder.setOnClickListener(v -> confirmOrder());
        btnCancelOrder.setOnClickListener(v -> showCancelDialog());
    }

    private void loadOrderDetail() {
        txtDetailMessage.setText("Đang tải chi tiết đơn hàng...");

        ApiClient.getApiService()
                .getAdminOrderDetail(orderId)
                .enqueue(new Callback<AdminOrderDetail>() {
                    @Override
                    public void onResponse(Call<AdminOrderDetail> call, Response<AdminOrderDetail> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentOrder = response.body();
                            showOrderDetail(currentOrder);
                            txtDetailMessage.setText("");
                        } else {
                            txtDetailMessage.setText("Không lấy được chi tiết đơn hàng");
                            Toast.makeText(AdminOrderDetailActivity.this, "Lỗi tải chi tiết đơn", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminOrderDetail> call, Throwable t) {
                        txtDetailMessage.setText("Lỗi API chi tiết đơn: " + t.getMessage());
                        Toast.makeText(AdminOrderDetailActivity.this, "Không kết nối được API", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showOrderDetail(AdminOrderDetail order) {
        txtOrderCode.setText("Mã đơn: #" + safe(order.getId()));
        txtReceiver.setText("Người nhận: " + safe(order.getReceiverName()));
        txtPhone.setText("SĐT: " + safe(order.getReceiverPhone()));
        txtAddress.setText("Địa chỉ: " + safe(order.getDeliveryAddress()));
        txtCreatedAt.setText("Ngày tạo: " + safe(order.getCreatedAt()));
        txtProductTotal.setText("Tiền hàng: " + formatMoney(order.getTotalProductPrice()));
        txtShippingFee.setText("Phí ship: " + formatMoney(order.getShippingFee()));
        txtDiscount.setText("Giảm giá: " + formatMoney(order.getDiscountAmount()));
        txtTotalAmount.setText("Tổng thanh toán: " + formatMoney(order.getTotalAmount()));
        txtPayment.setText("Thanh toán: " + safe(order.getPaymentMethod()) + " - " + translatePaymentStatus(order.getPaymentStatus()));
        txtOrderStatus.setText("Trạng thái đơn: " + translateOrderStatus(order.getOrderStatus()));
        txtNote.setText("Ghi chú: " + safe(order.getNote()));

        txtConfirmedInfo.setText(order.getConfirmedByAdminName() == null ?
                "Admin xác nhận: Chưa có" :
                "Admin xác nhận: " + order.getConfirmedByAdminName() + " - " + safe(order.getConfirmedAt()));

        txtCancelledInfo.setText(order.getCancelledByAdminName() == null ?
                "Admin hủy: Chưa có" :
                "Admin hủy: " + order.getCancelledByAdminName() + " - " + safe(order.getCancelledAt()));

        txtCancelReason.setText("Lý do hủy: " + safe(order.getCancelReason()));
        itemAdapter.setItems(order.getItems());
        updateActionButtons(order.getOrderStatus());
    }

    private void updateActionButtons(String status) {
        boolean isPending = "PENDING".equals(status);
        boolean isConfirmed = "CONFIRMED".equals(status);
        btnConfirmOrder.setVisibility(isPending ? View.VISIBLE : View.GONE);
        btnCancelOrder.setVisibility((isPending || isConfirmed) ? View.VISIBLE : View.GONE);
    }

    private void confirmOrder() {
        Long adminId = sessionManager.getUserId();
        if (adminId == null) {
            Toast.makeText(this, "Không tìm thấy adminId", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đơn hàng")
                .setMessage("Bạn chắc chắn muốn xác nhận đơn hàng này?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    AdminOrderActionRequest request = new AdminOrderActionRequest(adminId);
                    ApiClient.getApiService().confirmAdminOrder(orderId, request).enqueue(new Callback<AdminOrderDetail>() {
                        @Override
                        public void onResponse(Call<AdminOrderDetail> call, Response<AdminOrderDetail> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(AdminOrderDetailActivity.this, "Đã xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
                                currentOrder = response.body();
                                showOrderDetail(currentOrder);
                            } else {
                                Toast.makeText(AdminOrderDetailActivity.this, "Không xác nhận được đơn hàng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AdminOrderDetail> call, Throwable t) {
                            Toast.makeText(AdminOrderDetailActivity.this, "Lỗi API xác nhận: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }

    private void showCancelDialog() {
        Long adminId = sessionManager.getUserId();
        if (adminId == null) {
            Toast.makeText(this, "Không tìm thấy adminId", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText input = new EditText(this);
        input.setHint("Nhập lý do hủy đơn");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(2);

        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Vui lòng nhập lý do hủy")
                .setView(input)
                .setNegativeButton("Đóng", null)
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(this, "Lý do hủy không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cancelOrder(adminId, reason);
                })
                .show();
    }

    private void cancelOrder(Long adminId, String reason) {
        AdminOrderActionRequest request = new AdminOrderActionRequest(adminId, reason);
        ApiClient.getApiService().cancelAdminOrder(orderId, request).enqueue(new Callback<AdminOrderDetail>() {
            @Override
            public void onResponse(Call<AdminOrderDetail> call, Response<AdminOrderDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminOrderDetailActivity.this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    currentOrder = response.body();
                    showOrderDetail(currentOrder);
                } else {
                    Toast.makeText(AdminOrderDetailActivity.this, "Không hủy được đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminOrderDetail> call, Throwable t) {
                Toast.makeText(AdminOrderDetailActivity.this, "Lỗi API hủy: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0đ";
        return moneyFormatter.format(value) + "đ";
    }

    private String translateOrderStatus(String status) {
        if (status == null) return "Không rõ";
        switch (status) {
            case "PENDING": return "Chờ xử lý";
            case "CONFIRMED": return "Đã xác nhận";
            case "SHIPPING": return "Đang giao";
            case "COMPLETED": return "Hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }

    private String translatePaymentStatus(String status) {
        if (status == null) return "Không rõ";
        switch (status) {
            case "PAID": return "Đã thanh toán";
            case "UNPAID": return "Chưa thanh toán";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }
}
