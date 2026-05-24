package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.ProductVariant;

import java.util.ArrayList;
import java.util.List;

public class ProductVariantAdapter extends RecyclerView.Adapter<ProductVariantAdapter.VariantViewHolder> {

    private final List<ProductVariant> variantList = new ArrayList<>();

    public void setData(List<ProductVariant> variants) {
        variantList.clear();

        if (variants != null) {
            variantList.addAll(variants);
        }

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

        holder.txtVariantSizeColor.setText(
                "Size " + variant.getSize() + " - " + variant.getColor()
        );

        holder.txtVariantQuantity.setText(
                "Còn " + variant.getQuantity() + " sản phẩm"
        );
    }

    @Override
    public int getItemCount() {
        return variantList.size();
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