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
import com.example.fashionshopmobile.model.AdminProductResponse;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {

    private final List<AdminProductResponse> products = new ArrayList<>();
    private final OnAdminProductActionListener listener;
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public interface OnAdminProductActionListener {
        void onDetail(AdminProductResponse product);
        void onEdit(AdminProductResponse product);
        void onToggleStatus(AdminProductResponse product);
    }

    public AdminProductAdapter(OnAdminProductActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<AdminProductResponse> newProducts) {
        products.clear();

        if (newProducts != null) {
            products.addAll(newProducts);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        AdminProductResponse product = products.get(position);

        holder.txtName.setText(getSafeText(product.getName(), "Chưa có tên"));
        holder.txtId.setText("ID: " + product.getId());
        holder.txtCategory.setText("Danh mục: " + getSafeText(product.getCategoryName(), "Không có"));
        holder.txtBrand.setText("Brand: " + getSafeText(product.getBrand(), "Không có"));
        holder.txtPrice.setText(formatPriceText(product));
        holder.txtStatus.setText(formatStatus(product.getStatus()));

        boolean active = "ACTIVE".equalsIgnoreCase(product.getStatus());
        holder.btnToggle.setText(active ? "Ẩn" : "Hiện");

        String imageUrl = ImageUrlUtils.getFullImageUrl(product.getImageUrl());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgProduct);

        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetail(product);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(product);
            }
        });

        holder.btnToggle.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleStatus(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
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

    private String formatPriceText(AdminProductResponse product) {
        String price = formatMoney(product.getPrice());

        if (product.getSalePrice() != null) {
            return "Giá: " + price + " | Sale: " + formatMoney(product.getSalePrice());
        }

        return "Giá: " + price;
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0đ";
        }

        return priceFormatter.format(value) + "đ";
    }

    public static class AdminProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView txtName;
        TextView txtId;
        TextView txtCategory;
        TextView txtBrand;
        TextView txtPrice;
        TextView txtStatus;
        TextView btnDetail;
        TextView btnEdit;
        TextView btnToggle;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgAdminProduct);
            txtName = itemView.findViewById(R.id.txtAdminProductName);
            txtId = itemView.findViewById(R.id.txtAdminProductId);
            txtCategory = itemView.findViewById(R.id.txtAdminProductCategory);
            txtBrand = itemView.findViewById(R.id.txtAdminProductBrand);
            txtPrice = itemView.findViewById(R.id.txtAdminProductPrice);
            txtStatus = itemView.findViewById(R.id.txtAdminProductStatus);
            btnDetail = itemView.findViewById(R.id.btnDetailAdminProduct);
            btnEdit = itemView.findViewById(R.id.btnEditAdminProduct);
            btnToggle = itemView.findViewById(R.id.btnToggleAdminProduct);
        }
    }
}