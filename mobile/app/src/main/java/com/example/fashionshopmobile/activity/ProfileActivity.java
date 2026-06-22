package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.MainActivity;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.OrderSummary;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvFullName, tvEmail, btnCart;
    private TextView tvPendingCount, tvPackingCount, tvShippingCount, tvReviewCount;
    private TextView tvViewOrders;
    private RecyclerView rvSuggestedProducts;
    private BottomNavigationView bottomNavigation;

    private SessionManager sessionManager;
    private ProductAdapter productAdapter;

    private Long userId;

    private static final int SUGGEST_LIMIT = 6;
    private TextView layoutAddress, layoutResetPassword, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            goToLogin();
            return;
        }

        initViews();
        setupSuggestedProducts();
        setupClickEvents();
        setupBottomNavigation();

        showSessionUser();
        loadUserProfile();
        loadOrderStatus();
        loadSuggestedProducts();
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.imgAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        btnCart = findViewById(R.id.btnCart);

        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvPackingCount = findViewById(R.id.tvPackingCount);
        tvShippingCount = findViewById(R.id.tvShippingCount);
        tvReviewCount = findViewById(R.id.tvReviewCount);

        tvViewOrders = findViewById(R.id.tvViewOrders);
        rvSuggestedProducts = findViewById(R.id.rvSuggestedProducts);
        bottomNavigation = findViewById(R.id.bottomNavigation);


        layoutAddress = findViewById(R.id.layoutAddress);
        layoutResetPassword = findViewById(R.id.layoutResetPassword);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void showSessionUser() {
        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();

        if (fullName != null && !fullName.isEmpty()) {
            tvFullName.setText(fullName);
        } else {
            tvFullName.setText("Người dùng");
        }

        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
        } else {
            tvEmail.setText("Chưa có email");
        }
    }

    private void loadUserProfile() {
        ApiClient.getApiService().getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    tvFullName.setText(getDisplayName(user));
                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "Chưa có email");

                    sessionManager.saveUser(user);
                    loadAvatar(user.getAvatarUrl());
                } else {
                    Toast.makeText(ProfileActivity.this, "Không lấy được thông tin tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi tài khoản: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getDisplayName(User user) {
        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            return user.getFullName();
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            return user.getEmail();
        }

        return "Người dùng";
    }

    private void loadAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            imgAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            return;
        }

        Glide.with(this)
                .load(buildImageUrl(avatarUrl))
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(imgAvatar);
    }

    private String buildImageUrl(String imageUrl) {
        if (imageUrl.startsWith("http")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/")) {
            return "http://192.168.1.101:8080" + imageUrl;
        }

        return "http://192.168.1.101:8080/" + imageUrl;
    }

    private void loadOrderStatus() {
        ApiClient.getApiService().getOrdersByUserId(userId).enqueue(new Callback<List<OrderSummary>>() {
            @Override
            public void onResponse(Call<List<OrderSummary>> call, Response<List<OrderSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showOrderCount(response.body());
                } else {
                    Toast.makeText(ProfileActivity.this, "Không lấy được đơn mua", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderSummary>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi đơn mua: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOrderCount(List<OrderSummary> orders) {
        int pending = 0;
        int packing = 0;
        int shipping = 0;
        int review = 0;

        for (OrderSummary order : orders) {
            String status = order.getOrderStatus();

            if (status == null) {
                continue;
            }

            if (status.equals("PENDING")) {
                pending++;
            } else if (status.equals("CONFIRMED") || status.equals("PROCESSING") || status.equals("PACKING")) {
                packing++;
            } else if (status.equals("SHIPPING") || status.equals("DELIVERING")) {
                shipping++;
            } else if (status.equals("DELIVERED") || status.equals("COMPLETED")) {
                review++;
            }
        }

        tvPendingCount.setText(String.valueOf(pending));
        tvPackingCount.setText(String.valueOf(packing));
        tvShippingCount.setText(String.valueOf(shipping));
        tvReviewCount.setText(String.valueOf(review));
    }

    private void setupSuggestedProducts() {
        productAdapter = new ProductAdapter(product -> {
            Intent intent = new Intent(ProfileActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        rvSuggestedProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvSuggestedProducts.setAdapter(productAdapter);
    }

    private void loadSuggestedProducts() {
        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    if (products.isEmpty()) {
                        productAdapter.setData(new ArrayList<>());
                        return;
                    }

                    int endIndex = Math.min(products.size(), SUGGEST_LIMIT);
                    List<Product> suggestedProducts = new ArrayList<>(products.subList(0, endIndex));

                    productAdapter.setData(suggestedProducts);
                } else {
                    Toast.makeText(ProfileActivity.this, "Không lấy được gợi ý sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi gợi ý: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickEvents() {
        btnCart.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng giỏ hàng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        tvViewOrders.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng lịch sử đơn hàng đang phát triển", Toast.LENGTH_SHORT).show();
        });
        imgAvatar.setOnClickListener(v -> openEditProfile());

        tvFullName.setOnClickListener(v -> openEditProfile());

        layoutAddress.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddressActivity.class);
            startActivity(intent);
        });

        layoutResetPassword.setOnClickListener(v -> sendResetPasswordEmail());

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }
    private void openEditProfile() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }

            if (itemId == R.id.nav_orders) {
                Toast.makeText(this, "Chức năng đơn hàng đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (itemId == R.id.nav_store) {
                Toast.makeText(this, "Chức năng cửa hàng đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (itemId == R.id.nav_profile) {
                return true;
            }

            return false;
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendResetPasswordEmail() {
        String email = sessionManager.getEmail();

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Đặt lại mật khẩu")
                .setMessage("Hệ thống sẽ gửi email đặt lại mật khẩu đến:\n" + email)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Gửi email", (dialog, which) -> {
                    FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "Gửi email thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    sessionManager.logout();

                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager != null && sessionManager.getUserId() != null) {
            showSessionUser();
            loadUserProfile();
        }
    }


}