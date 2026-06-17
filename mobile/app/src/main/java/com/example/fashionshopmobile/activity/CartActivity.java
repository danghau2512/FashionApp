package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.CartAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.request.UpdateCartItemRequest;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private TextView btnBackCart;
    private TextView txtCartTotalProducts;
    private TextView txtEmptyCart;
    private TextView txtSelectedCount;
    private TextView txtSelectedTotalPrice;

    private CheckBox cbSelectAllCart;
    private MaterialButton btnCheckout;
    private ProgressBar progressCart;
    private RecyclerView recyclerCartItems;

    private CartAdapter cartAdapter;
    private SessionManager sessionManager;

    private boolean changingSelectAll = false;

    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        setupClickEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void initViews() {
        btnBackCart = findViewById(R.id.btnBackCart);
        txtCartTotalProducts = findViewById(R.id.txtCartTotalProducts);
        txtEmptyCart = findViewById(R.id.txtEmptyCart);
        txtSelectedCount = findViewById(R.id.txtSelectedCount);
        txtSelectedTotalPrice = findViewById(R.id.txtSelectedTotalPrice);

        cbSelectAllCart = findViewById(R.id.cbSelectAllCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        progressCart = findViewById(R.id.progressCart);
        recyclerCartItems = findViewById(R.id.recyclerCartItems);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(new CartAdapter.OnCartActionListener() {
            @Override
            public void onSelectionChanged() {
                updateSummary();
            }

            @Override
            public void onIncrease(CartItem cartItem) {
                int currentQuantity = getQuantity(cartItem);
                int stockQuantity = getStockQuantity(cartItem);

                if (currentQuantity >= stockQuantity) {
                    Toast.makeText(CartActivity.this, "Số lượng đã đạt tối đa trong kho", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateCartQuantity(cartItem, currentQuantity + 1);
            }

            @Override
            public void onDecrease(CartItem cartItem) {
                int currentQuantity = getQuantity(cartItem);

                if (currentQuantity <= 1) {
                    Toast.makeText(CartActivity.this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateCartQuantity(cartItem, currentQuantity - 1);
            }

            @Override
            public void onDelete(CartItem cartItem) {
                deleteCartItem(cartItem);
            }
        });

        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartItems.setAdapter(cartAdapter);
    }

    private void setupClickEvents() {
        btnBackCart.setOnClickListener(v -> finish());

        cbSelectAllCart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (changingSelectAll) {
                return;
            }

            cartAdapter.setAllSelected(isChecked);
        });

        btnCheckout.setOnClickListener(v -> checkoutSelectedItems());
    }

    private void loadCartItems() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setLoading(true);

        ApiClient.getApiService().getCartByUserId(userId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    cartAdapter.setData(response.body());
                    updateSummary();
                } else {
                    Toast.makeText(CartActivity.this, "Không lấy được giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(CartActivity.this, "Lỗi API giỏ hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateCartQuantity(CartItem cartItem, int newQuantity) {
        if (cartItem == null || cartItem.getId() == null) {
            return;
        }

        if (newQuantity < 1) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newQuantity > getStockQuantity(cartItem)) {
            Toast.makeText(this, "Số lượng trong kho không đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateCartItemRequest request = new UpdateCartItemRequest(newQuantity);

        ApiClient.getApiService().updateCartItem(cartItem.getId(), request)
                .enqueue(new Callback<CartItem>() {
                    @Override
                    public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            cartAdapter.replaceItem(response.body());
                            updateSummary();
                        } else {
                            Toast.makeText(CartActivity.this, "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartItem> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Lỗi cập nhật giỏ hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void deleteCartItem(CartItem cartItem) {
        if (cartItem == null || cartItem.getId() == null) {
            return;
        }

        ApiClient.getApiService().deleteCartItem(cartItem.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            cartAdapter.removeItemById(cartItem.getId());
                            updateSummary();

                            Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CartActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Lỗi xóa giỏ hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateSummary() {
        int totalQuantityAll = cartAdapter.getTotalQuantityAll();
        int selectedQuantity = cartAdapter.getSelectedQuantity();
        BigDecimal selectedTotal = cartAdapter.getSelectedTotal();

        txtCartTotalProducts.setText("Tổng tất cả sản phẩm: " + totalQuantityAll);

        txtSelectedCount.setText("Đã chọn: " + selectedQuantity + " sản phẩm");
        txtSelectedTotalPrice.setText("Tổng tiền: " + formatPrice(selectedTotal));

        btnCheckout.setEnabled(selectedQuantity > 0);
        btnCheckout.setText(selectedQuantity > 0 ? "Mua hàng" : "Mua hàng");

        txtEmptyCart.setVisibility(cartAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        recyclerCartItems.setVisibility(cartAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);

        changingSelectAll = true;
        cbSelectAllCart.setChecked(cartAdapter.isAllSelected());
        changingSelectAll = false;
    }

    private void checkoutSelectedItems() {
        List<CartItem> selectedItems = cartAdapter.getSelectedItems();

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sản phẩm cần mua", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedQuantity = cartAdapter.getSelectedQuantity();
        BigDecimal selectedTotal = cartAdapter.getSelectedTotal();

        Toast.makeText(
                this,
                "Đã chọn " + selectedQuantity + " sản phẩm, tổng tiền " + formatPrice(selectedTotal)
                        + ". Màn thanh toán sẽ làm ở bước sau.",
                Toast.LENGTH_LONG
        ).show();
    }

    private int getQuantity(CartItem cartItem) {
        if (cartItem == null || cartItem.getQuantity() == null) {
            return 0;
        }

        return cartItem.getQuantity();
    }

    private int getStockQuantity(CartItem cartItem) {
        if (cartItem == null || cartItem.getStockQuantity() == null) {
            return 0;
        }

        return cartItem.getStockQuantity();
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        return priceFormatter.format(price) + "đ";
    }

    private void setLoading(boolean loading) {
        progressCart.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}