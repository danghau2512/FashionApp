package com.example.fashionshopmobile.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.activity.ProductDetailActivity;
import com.example.fashionshopmobile.model.OrderItemResponse;
import com.example.fashionshopmobile.utils.ImageUrlUtils;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class OrderDetailProductAdapter extends RecyclerView.Adapter<OrderDetailProductAdapter.OrderDetailProductViewHolder> {

    private final List<OrderItemResponse> items = new ArrayList<>();
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    private boolean canReviewOrder = false;
    private boolean reviewStateLoaded = false;
    private final Set<Long> reviewableOrderItemIds = new HashSet<>();

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

        bindReviewButton(holder, item);
    }
    private void bindReviewButton(OrderDetailProductViewHolder holder, OrderItemResponse item) {
        if (!canReviewOrder || item.getProductId() == null || item.getId() == null) {
            holder.btnReviewProduct.setVisibility(View.GONE);
            holder.btnReviewProduct.setOnClickListener(null);
            return;
        }

        holder.btnReviewProduct.setVisibility(View.VISIBLE);

        if (!reviewStateLoaded) {
            holder.btnReviewProduct.setText("Đang kiểm tra");
            holder.btnReviewProduct.setEnabled(false);
            holder.btnReviewProduct.setAlpha(0.5f);
            holder.btnReviewProduct.setOnClickListener(null);
            return;
        }

        boolean canReviewThisItem = reviewableOrderItemIds.contains(item.getId());

        if (canReviewThisItem) {
            holder.btnReviewProduct.setText("Đánh giá");
            holder.btnReviewProduct.setEnabled(true);
            holder.btnReviewProduct.setAlpha(1f);
            holder.btnReviewProduct.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_pink)
            ));

            holder.btnReviewProduct.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", item.getProductId());
                intent.putExtra("open_review_section", true);
                intent.putExtra("auto_open_review_dialog", true);
                intent.putExtra("review_order_item_id", item.getId());

                holder.itemView.getContext().startActivity(intent);
            });
        } else {
            holder.btnReviewProduct.setText("Đã đánh giá");
            holder.btnReviewProduct.setEnabled(false);
            holder.btnReviewProduct.setAlpha(0.45f);
            holder.btnReviewProduct.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_text)
            ));
            holder.btnReviewProduct.setOnClickListener(null);
        }
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
    public void setReviewState(boolean canReviewOrder, boolean reviewStateLoaded, Set<Long> newReviewableOrderItemIds) {
        this.canReviewOrder = canReviewOrder;
        this.reviewStateLoaded = reviewStateLoaded;

        reviewableOrderItemIds.clear();

        if (newReviewableOrderItemIds != null) {
            reviewableOrderItemIds.addAll(newReviewableOrderItemIds);
        }

        notifyDataSetChanged();
    }

    static class OrderDetailProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName;
        TextView tvVariant;
        TextView tvPrice;
        TextView tvQuantity;
        TextView tvSubtotal;
        MaterialButton btnReviewProduct;

        public OrderDetailProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgOrderDetailProduct);
            tvName = itemView.findViewById(R.id.tvOrderDetailProductName);
            tvVariant = itemView.findViewById(R.id.tvOrderDetailVariant);
            tvPrice = itemView.findViewById(R.id.tvOrderDetailPrice);
            tvQuantity = itemView.findViewById(R.id.tvOrderDetailQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvOrderDetailSubtotal);
            btnReviewProduct = itemView.findViewById(R.id.btnReviewProduct);
        }
    }

}