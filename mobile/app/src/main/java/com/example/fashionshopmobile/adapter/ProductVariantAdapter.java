package com.example.fashionshopmobile.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.ProductVariant;

import java.util.ArrayList;
import java.util.List;

public class ProductVariantAdapter extends RecyclerView.Adapter<ProductVariantAdapter.VariantViewHolder> {

    private final List<ProductVariant> variantList = new ArrayList<>();
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnVariantClickListener listener;

    public interface OnVariantClickListener {
        void onVariantClick(ProductVariant variant);
    }

    public ProductVariantAdapter(OnVariantClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<ProductVariant> variants) {
        variantList.clear();

        if (variants != null) {
            variantList.addAll(variants);
        }

        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_variant, parent, false);

        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        ProductVariant variant = variantList.get(position);

        String color = variant.getColor() != null ? variant.getColor() : "";
        String size = variant.getSize() != null ? variant.getSize() : "";

        holder.txtVariantSizeColor.setText(color + " · " + size);

        int stock = getStock(variant);

        if (stock > 0) {
            holder.txtVariantQuantity.setText("Còn " + stock);
            holder.txtVariantQuantity.setTextColor(Color.parseColor("#777777"));
        } else {
            holder.txtVariantQuantity.setText("Hết hàng");
            holder.txtVariantQuantity.setTextColor(Color.parseColor("#D32F2F"));
        }

        boolean isSelected = position == selectedPosition;

        if (isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_variant_selected);
            holder.txtVariantSizeColor.setTextColor(Color.parseColor("#EE4D2D"));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_variant_normal);
            holder.txtVariantSizeColor.setTextColor(Color.parseColor("#222222"));
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            ProductVariant clickedVariant = variantList.get(currentPosition);

            int oldPosition = selectedPosition;
            selectedPosition = currentPosition;

            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition);
            }

            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onVariantClick(clickedVariant);
            }

            if (getStock(clickedVariant) <= 0) {
                Toast.makeText(
                        holder.itemView.getContext(),
                        "Phân loại này đã hết hàng",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return variantList.size();
    }

    private int getStock(ProductVariant variant) {
        if (variant == null || variant.getQuantity() == null) {
            return 0;
        }

        return variant.getQuantity();
    }

    static class VariantViewHolder extends RecyclerView.ViewHolder {

        TextView txtVariantSizeColor;
        TextView txtVariantQuantity;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);

            txtVariantSizeColor = itemView.findViewById(R.id.txtVariantSizeColor);
            txtVariantQuantity = itemView.findViewById(R.id.txtVariantQuantity);
        }
    }
}