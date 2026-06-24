package com.example.fashionshopmobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.activity.ProductDetailActivity;
import com.example.fashionshopmobile.model.OrderItemResponse;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailProductAdapter extends RecyclerView.Adapter<OrderDetailProductAdapter.OrderDetailProductViewHolder> {

    private final List<OrderItemResponse> items = new ArrayList<>();
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void setData(List<OrderItemResponse> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDetailProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail_product, parent, false);

        return new OrderDetailProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailProductViewHolder holder, int position) {
        OrderItemResponse item = items.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvVariant.setText(getVariantText(item.getColor(), item.getSize()));
        holder.tvPrice.setText(formatPrice(item.getPrice()));
        holder.tvQuantity.setText("x" + getQuantity(item.getQuantity()));
        holder.tvSubtotal.setText("Thành tiền: " + formatPrice(item.getSubtotal()));

        String imageUrl = ImageUrlUtils.getFullImageUrl(item.getProductImageUrl());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgProduct);

        holder.imgProduct.setOnClickListener(v -> {
            if (item.getProductId() == null) {
                Toast.makeText(
                        holder.itemView.getContext(),
                        "Không tìm thấy sản phẩm",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Intent intent = new Intent(holder.itemView.getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", item.getProductId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String getVariantText(String color, String size) {
        String colorText = color == null || color.trim().isEmpty() ? "Không có màu" : color;
        String sizeText = size == null || size.trim().isEmpty() ? "Không có size" : "Size " + size;

        return colorText + " · " + sizeText;
    }

    private int getQuantity(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        return priceFormatter.format(price) + "đ";
    }

    static class OrderDetailProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName;
        TextView tvVariant;
        TextView tvPrice;
        TextView tvQuantity;
        TextView tvSubtotal;

        public OrderDetailProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgOrderDetailProduct);
            tvName = itemView.findViewById(R.id.tvOrderDetailProductName);
            tvVariant = itemView.findViewById(R.id.tvOrderDetailVariant);
            tvPrice = itemView.findViewById(R.id.tvOrderDetailPrice);
            tvQuantity = itemView.findViewById(R.id.tvOrderDetailQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvOrderDetailSubtotal);
        }
    }
}