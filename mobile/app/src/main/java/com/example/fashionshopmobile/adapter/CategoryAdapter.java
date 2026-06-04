package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvCategoryName.setText(category.getName());

        if (position == selectedPosition) {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_category_selected);
            holder.tvCategoryName.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white)
            );
            holder.tvCategoryName.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.tvCategoryName.setBackgroundResource(R.drawable.bg_category);
            holder.tvCategoryName.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_text)
            );
            holder.tvCategoryName.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();

            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}