package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.AdminOrderSummary;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onViewDetail(AdminOrderSummary order);
    }

    private final List<AdminOrderSummary> orders = new ArrayList<>();
    private final OnOrderClickListener listener;
    private final NumberFormat moneyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public AdminOrderAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<AdminOrderSummary> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        AdminOrderSummary order = orders.get(position);
        holder.txtOrderId.setText("#" + safe(order.getId()));
        holder.txtReceiver.setText("Người nhận: " + safe(order.getReceiverName()));
        holder.txtPhone.setText("SĐT: " + safe(order.getReceiverPhone()));
        holder.txtTotal.setText("Tổng: " + formatMoney(order.getTotalAmount()));
        holder.txtPayment.setText("Thanh toán: " + safe(order.getPaymentMethod()) + " - " + translatePaymentStatus(order.getPaymentStatus()));
        holder.txtStatus.setText("Trạng thái: " + translateOrderStatus(order.getOrderStatus()));
        holder.txtCreatedAt.setText("Ngày tạo: " + safe(order.getCreatedAt()));
        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetail(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
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
            case "SHIPPING": return "Đang vận chuyển";
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

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtReceiver, txtPhone, txtTotal, txtPayment, txtStatus, txtCreatedAt;
        Button btnDetail;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtReceiver = itemView.findViewById(R.id.txtReceiver);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtPayment = itemView.findViewById(R.id.txtPayment);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}
