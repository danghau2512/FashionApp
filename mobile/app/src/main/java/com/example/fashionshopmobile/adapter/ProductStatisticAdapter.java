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
import com.example.fashionshopmobile.model.ProductStatistic;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductStatisticAdapter extends RecyclerView.Adapter<ProductStatisticAdapter.ProductStatisticViewHolder> {

    private final List<ProductStatistic> products = new ArrayList<>();
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void setData(List<ProductStatistic> data) {
        products.clear();

        if (data != null) {
            products.addAll(data);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductStatisticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_statistic, parent, false);
        return new ProductStatisticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductStatisticViewHolder holder, int position) {
        ProductStatistic product = products.get(position);

        holder.txtIndex.setText(String.valueOf(position + 1));
        holder.txtProductName.setText(product.getProductName() != null ? product.getProductName() : "Không có tên");
        holder.txtCategoryName.setText(product.getCategoryName() != null ? product.getCategoryName() : "Chưa có danh mục");
        holder.txtProductPrice.setText(formatPrice(getDisplayPrice(product)));
        holder.txtSoldQuantity.setText("Đã bán: " + getLongValue(product.getSoldQuantity()));

        String fullImageUrl = ImageUrlUtils.getFullImageUrl(product.getImageUrl());

        Glide.with(holder.imgProduct.getContext())
                .load(fullImageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private BigDecimal getDisplayPrice(ProductStatistic product) {
        if (product.getSalePrice() != null && product.getSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            return product.getSalePrice();
        }

        return product.getPrice();
    }

    private long getLongValue(Long value) {
        return value != null ? value : 0L;
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        return priceFormatter.format(price) + "đ";
    }

    static class ProductStatisticViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtIndex;
        private final ImageView imgProduct;
        private final TextView txtProductName;
        private final TextView txtCategoryName;
        private final TextView txtProductPrice;
        private final TextView txtSoldQuantity;

        public ProductStatisticViewHolder(@NonNull View itemView) {
            super(itemView);

            txtIndex = itemView.findViewById(R.id.txtIndex);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtSoldQuantity = itemView.findViewById(R.id.txtSoldQuantity);
        }
    }
}