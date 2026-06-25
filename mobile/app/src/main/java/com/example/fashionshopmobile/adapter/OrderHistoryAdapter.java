package com.example.fashionshopmobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.activity.OrderDetailActivity;
import com.example.fashionshopmobile.model.OrderSummary;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.utils.ImageUrlUtils;
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final List<OrderSummary> orders = new ArrayList<>();
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void setData(List<OrderSummary> newOrders) {
        orders.clear();

        if (newOrders != null) {
            orders.addAll(newOrders);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderSummary order = orders.get(position);
        String imageUrl = ImageUrlUtils.getFullImageUrl(order.getProductImageUrl());

        holder.tvOrderCode.setText("Đơn hàng #" + order.getId());
        holder.tvOrderStatus.setText(getStatusText(order.getOrderStatus()));
        holder.tvOrderDate.setText("Ngày đặt: " + formatDate(order.getCreatedAt()));
        holder.tvReceiverName.setText("Người nhận: " + getTextOrDefault(order.getReceiverName(), "Chưa có"));
        holder.tvPaymentInfo.setText("Thanh toán: " + getPaymentText(order.getPaymentMethod(), order.getPaymentStatus()));
        holder.tvOrderTotal.setText("Tổng tiền: " + formatPrice(order.getTotalAmount()));
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgOrderProduct);
        View.OnClickListener openOrderDetailListener = v -> {
            Intent intent = new Intent(holder.itemView.getContext(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            holder.itemView.getContext().startActivity(intent);
        };

        holder.itemView.setOnClickListener(openOrderDetailListener);
        holder.imgOrderProduct.setOnClickListener(openOrderDetailListener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
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
        String methodText = getTextOrDefault(method, "Chưa có");
        String statusText = getPaymentStatusText(status);

        return methodText + " - " + statusText;
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

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderCode;
        TextView tvOrderStatus;
        TextView tvOrderDate;
        TextView tvReceiverName;
        TextView tvPaymentInfo;
        TextView tvOrderTotal;
        ImageView imgOrderProduct;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvPaymentInfo = itemView.findViewById(R.id.tvPaymentInfo);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            imgOrderProduct = itemView.findViewById(R.id.imgOrderProduct);
        }
    }
}