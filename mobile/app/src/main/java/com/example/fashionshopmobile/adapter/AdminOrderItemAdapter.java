package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.AdminOrderItem;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderItemAdapter extends RecyclerView.Adapter<AdminOrderItemAdapter.ItemViewHolder> {

    private final List<AdminOrderItem> items = new ArrayList<>();
    private final NumberFormat moneyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void setItems(List<AdminOrderItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order_detail_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        AdminOrderItem item = items.get(position);
        holder.txtProductName.setText(safe(item.getProductName()));
        holder.txtVariant.setText("Size: " + safe(item.getSize()) + " | Màu: " + safe(item.getColor()));
        holder.txtQuantity.setText("SL: " + safe(item.getQuantity()));
        holder.txtPrice.setText("Giá: " + formatMoney(item.getPrice()));
        holder.txtSubtotal.setText("Thành tiền: " + formatMoney(item.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0đ";
        return moneyFormatter.format(value) + "đ";
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtVariant, txtQuantity, txtPrice, txtSubtotal;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtVariant = itemView.findViewById(R.id.txtVariant);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtSubtotal = itemView.findViewById(R.id.txtSubtotal);
        }
    }
}
