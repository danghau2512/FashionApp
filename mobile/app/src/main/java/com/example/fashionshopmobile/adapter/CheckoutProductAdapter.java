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
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutProductAdapter extends RecyclerView.Adapter<CheckoutProductAdapter.CheckoutViewHolder> {

    private final List<CartItem> items = new ArrayList<>();
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void setData(List<CartItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_product, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem item = items.get(position);

        holder.tvName.setText(item.getProductName());

        String color = item.getColor() == null ? "" : item.getColor();
        String size = item.getSize() == null ? "" : item.getSize();
        holder.tvVariant.setText(color + " · Size " + size);

        holder.tvPrice.setText(formatPrice(item.getPrice()));
        holder.tvQuantity.setText("x" + item.getQuantity());

        String imageUrl = ImageUrlUtils.getFullImageUrl(item.getProductImageUrl());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        return priceFormatter.format(price) + "đ";
    }

    static class CheckoutViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName;
        TextView tvVariant;
        TextView tvPrice;
        TextView tvQuantity;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgCheckoutProduct);
            tvName = itemView.findViewById(R.id.tvCheckoutProductName);
            tvVariant = itemView.findViewById(R.id.tvCheckoutVariant);
            tvPrice = itemView.findViewById(R.id.tvCheckoutPrice);
            tvQuantity = itemView.findViewById(R.id.tvCheckoutQuantity);
        }
    }
}