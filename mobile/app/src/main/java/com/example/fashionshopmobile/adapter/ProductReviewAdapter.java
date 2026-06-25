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
import com.example.fashionshopmobile.model.ProductReview;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ProductReviewViewHolder> {

    private List<ProductReview> reviews = new ArrayList<>();

    public void setData(List<ProductReview> reviews) {
        this.reviews = reviews == null ? new ArrayList<>() : reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_review, parent, false);
        return new ProductReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductReviewViewHolder holder, int position) {
        ProductReview review = reviews.get(position);

        holder.txtReviewUserName.setText(getTextOrDefault(review.getUserName(), "Khách hàng"));
        holder.txtReviewDate.setText(formatDate(review.getCreatedAt()));
        holder.txtReviewRating.setText(buildStars(review.getRating()));
        holder.txtReviewComment.setText(getTextOrDefault(review.getComment(), "Không có nội dung đánh giá"));

        String avatarUrl = ImageUrlUtils.getFullImageUrl(review.getUserAvatarUrl());

        Glide.with(holder.itemView.getContext())
                .load(avatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgReviewAvatar);
        String variantText = buildVariantText(review);

        if (variantText.isEmpty()) {
            holder.txtReviewVariant.setVisibility(View.GONE);
        } else {
            holder.txtReviewVariant.setVisibility(View.VISIBLE);
            holder.txtReviewVariant.setText(variantText);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private String buildStars(Integer rating) {
        int value = rating == null ? 0 : rating;
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i <= 5; i++) {
            builder.append(i <= value ? "★" : "☆");
        }

        return builder.toString();
    }

    private String formatDate(String createdAt) {
        if (createdAt == null || createdAt.trim().isEmpty()) {
            return "";
        }

        if (createdAt.length() >= 10) {
            return createdAt.substring(0, 10);
        }

        return createdAt;
    }

    private String getTextOrDefault(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }
    private String buildVariantText(ProductReview review) {
        String color = review.getColor();
        String size = review.getSize();

        boolean hasColor = color != null && !color.trim().isEmpty();
        boolean hasSize = size != null && !size.trim().isEmpty();

        if (!hasColor && !hasSize) {
            return "";
        }

        if (hasColor && hasSize) {
            return "Phân loại: " + color + " / " + size;
        }

        if (hasColor) {
            return "Phân loại: " + color;
        }

        return "Phân loại: " + size;
    }
    public static class ProductReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgReviewAvatar;
        TextView txtReviewUserName;
        TextView txtReviewDate;
        TextView txtReviewRating;
        TextView txtReviewComment;
        TextView txtReviewVariant;

        public ProductReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            imgReviewAvatar = itemView.findViewById(R.id.imgReviewAvatar);
            txtReviewUserName = itemView.findViewById(R.id.txtReviewUserName);
            txtReviewDate = itemView.findViewById(R.id.txtReviewDate);
            txtReviewRating = itemView.findViewById(R.id.txtReviewRating);
            txtReviewComment = itemView.findViewById(R.id.txtReviewComment);
            txtReviewVariant = itemView.findViewById(R.id.txtReviewVariant);
        }
    }
}