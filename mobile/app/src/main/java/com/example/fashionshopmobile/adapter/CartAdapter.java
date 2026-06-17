package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems = new ArrayList<>();
    private final Set<Long> selectedIds = new HashSet<>();
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    private OnCartActionListener listener;

    public interface OnCartActionListener {
        void onSelectionChanged();
        void onIncrease(CartItem cartItem);
        void onDecrease(CartItem cartItem);
        void onDelete(CartItem cartItem);
    }

    public CartAdapter(OnCartActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<CartItem> items) {
        cartItems.clear();

        if (items != null) {
            cartItems.addAll(items);
        }

        selectedIds.clear();
        notifyDataSetChanged();

        if (listener != null) {
            listener.onSelectionChanged();
        }
    }

    public void replaceItem(CartItem updatedItem) {
        if (updatedItem == null || updatedItem.getId() == null) {
            return;
        }

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem oldItem = cartItems.get(i);

            if (oldItem.getId().equals(updatedItem.getId())) {
                cartItems.set(i, updatedItem);
                notifyItemChanged(i);
                break;
            }
        }

        if (listener != null) {
            listener.onSelectionChanged();
        }
    }

    public void removeItemById(Long cartItemId) {
        if (cartItemId == null) {
            return;
        }

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);

            if (cartItemId.equals(item.getId())) {
                cartItems.remove(i);
                selectedIds.remove(cartItemId);
                notifyItemRemoved(i);
                break;
            }
        }

        if (listener != null) {
            listener.onSelectionChanged();
        }
    }

    public void setAllSelected(boolean selected) {
        selectedIds.clear();

        if (selected) {
            for (CartItem item : cartItems) {
                if (item.getId() != null) {
                    selectedIds.add(item.getId());
                }
            }
        }

        notifyDataSetChanged();

        if (listener != null) {
            listener.onSelectionChanged();
        }
    }

    public boolean isAllSelected() {
        return !cartItems.isEmpty() && selectedIds.size() == cartItems.size();
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            if (item.getId() != null && selectedIds.contains(item.getId())) {
                selectedItems.add(item);
            }
        }

        return selectedItems;
    }

    public int getTotalQuantityAll() {
        int total = 0;

        for (CartItem item : cartItems) {
            total += getQuantity(item);
        }

        return total;
    }

    public int getSelectedQuantity() {
        int total = 0;

        for (CartItem item : getSelectedItems()) {
            total += getQuantity(item);
        }

        return total;
    }

    public BigDecimal getSelectedTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : getSelectedItems()) {
            total = total.add(getSubtotal(item));
        }

        return total;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.txtCartProductName.setText(getTextOrDefault(item.getProductName(), "Tên sản phẩm"));

        String color = getTextOrDefault(item.getColor(), "");
        String size = getTextOrDefault(item.getSize(), "");
        holder.txtCartVariant.setText(color + " · " + size);

        holder.txtCartStock.setText("Kho: " + getStockQuantity(item));

        holder.txtCartPrice.setText(formatPrice(item.getPrice()));
        holder.txtCartQuantity.setText(String.valueOf(getQuantity(item)));

        String imageUrl = ImageUrlUtils.getFullImageUrl(item.getProductImageUrl());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.imgCartProduct);

        holder.cbSelectCartItem.setOnCheckedChangeListener(null);
        holder.cbSelectCartItem.setChecked(item.getId() != null && selectedIds.contains(item.getId()));

        holder.cbSelectCartItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (item.getId() == null) {
                return;
            }

            if (isChecked) {
                selectedIds.add(item.getId());
            } else {
                selectedIds.remove(item.getId());
            }

            if (listener != null) {
                listener.onSelectionChanged();
            }
        });

        holder.btnIncreaseCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncrease(item);
            }
        });

        holder.btnDecreaseCart.setOnClickListener(v -> {
            if (getQuantity(item) <= 1) {
                Toast.makeText(holder.itemView.getContext(), "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) {
                listener.onDecrease(item);
            }
        });

        holder.btnDeleteCartItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private int getQuantity(CartItem item) {
        if (item == null || item.getQuantity() == null) {
            return 0;
        }

        return item.getQuantity();
    }

    private int getStockQuantity(CartItem item) {
        if (item == null || item.getStockQuantity() == null) {
            return 0;
        }

        return item.getStockQuantity();
    }

    private BigDecimal getSubtotal(CartItem item) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        if (item.getSubtotal() != null) {
            return item.getSubtotal();
        }

        if (item.getPrice() == null || item.getQuantity() == null) {
            return BigDecimal.ZERO;
        }

        return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "Liên hệ";
        }

        return priceFormatter.format(price) + "đ";
    }

    private String getTextOrDefault(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbSelectCartItem;
        ImageView imgCartProduct;
        TextView txtCartProductName;
        TextView txtCartVariant;
        TextView txtCartStock;
        TextView txtCartPrice;
        TextView txtCartQuantity;
        TextView btnDecreaseCart;
        TextView btnIncreaseCart;
        TextView btnDeleteCartItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cbSelectCartItem = itemView.findViewById(R.id.cbSelectCartItem);
            imgCartProduct = itemView.findViewById(R.id.imgCartProduct);
            txtCartProductName = itemView.findViewById(R.id.txtCartProductName);
            txtCartVariant = itemView.findViewById(R.id.txtCartVariant);
            txtCartStock = itemView.findViewById(R.id.txtCartStock);
            txtCartPrice = itemView.findViewById(R.id.txtCartPrice);
            txtCartQuantity = itemView.findViewById(R.id.txtCartQuantity);
            btnDecreaseCart = itemView.findViewById(R.id.btnDecreaseCart);
            btnIncreaseCart = itemView.findViewById(R.id.btnIncreaseCart);
            btnDeleteCartItem = itemView.findViewById(R.id.btnDeleteCartItem);
        }
    }
}