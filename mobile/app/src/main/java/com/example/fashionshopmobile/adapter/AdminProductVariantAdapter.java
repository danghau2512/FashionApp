package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.AdminProductVariantResponse;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminProductVariantAdapter extends RecyclerView.Adapter<AdminProductVariantAdapter.AdminVariantViewHolder> {

    private final List<AdminProductVariantResponse> variants = new ArrayList<>();
    private final OnAdminVariantActionListener listener;

    public interface OnAdminVariantActionListener {
        void onEdit(AdminProductVariantResponse variant);
        void onToggleStatus(AdminProductVariantResponse variant);
    }

    public AdminProductVariantAdapter(OnAdminVariantActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<AdminProductVariantResponse> newVariants) {
        variants.clear();

        if (newVariants != null) {
            variants.addAll(newVariants);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminVariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product_variant, parent, false);
        return new AdminVariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminVariantViewHolder holder, int position) {
        AdminProductVariantResponse variant = variants.get(position);

        holder.txtTitle.setText("Size " + getSafeText(variant.getSize(), "N/A")
                + " - Màu " + getSafeText(variant.getColor(), "N/A"));

        holder.txtId.setText("ID: " + variant.getId());
        holder.txtQuantity.setText("Số lượng: " + (variant.getQuantity() != null ? variant.getQuantity() : 0));
        holder.txtImageUrl.setText(getSafeText(variant.getImageUrl(), "Chưa có ảnh"));
        holder.txtStatus.setText(formatStatus(variant.getStatus()));

        boolean active = "ACTIVE".equalsIgnoreCase(variant.getStatus());
        holder.btnToggle.setText(active ? "Ẩn" : "Hiện");

        String imageUrl = ImageUrlUtils.getFullImageUrl(variant.getImageUrl());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgVariant);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(variant);
            }
        });

        holder.btnToggle.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleStatus(variant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    private String getSafeText(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    private String formatStatus(String status) {
        if ("ACTIVE".equalsIgnoreCase(status)) {
            return "Đang bán";
        }

        if ("INACTIVE".equalsIgnoreCase(status)) {
            return "Đã ẩn";
        }

        return "Không rõ";
    }

    public static class AdminVariantViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVariant;
        TextView txtTitle;
        TextView txtId;
        TextView txtQuantity;
        TextView txtImageUrl;
        TextView txtStatus;
        TextView btnEdit;
        TextView btnToggle;

        public AdminVariantViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVariant = itemView.findViewById(R.id.imgAdminVariant);
            txtTitle = itemView.findViewById(R.id.txtAdminVariantTitle);
            txtId = itemView.findViewById(R.id.txtAdminVariantId);
            txtQuantity = itemView.findViewById(R.id.txtAdminVariantQuantity);
            txtImageUrl = itemView.findViewById(R.id.txtAdminVariantImageUrl);
            txtStatus = itemView.findViewById(R.id.txtAdminVariantStatus);
            btnEdit = itemView.findViewById(R.id.btnEditAdminVariant);
            btnToggle = itemView.findViewById(R.id.btnToggleAdminVariant);
        }
    }
}