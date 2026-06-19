package com.example.fashionshopmobile;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.activity.ProductDetailActivity;
import com.example.fashionshopmobile.activity.ProductListActivity;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.fashionshopmobile.activity.ProfileActivity;

import com.example.fashionshopmobile.adapter.CategoryAdapter;
import com.example.fashionshopmobile.model.Category;

import java.util.ArrayList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.activity.EditProfileActivity;
import com.example.fashionshopmobile.model.User;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello, btnCart, tvProductSectionTitle, tvViewAllProducts;
    private ImageView imgHomeAvatar;
    private RecyclerView rvProducts;
    private BottomNavigationView bottomNavigation;

    private ProductAdapter productAdapter;
    private SessionManager sessionManager;
    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private Long currentCategoryId = 0L;
    private String currentCategoryName = "Tất cả sản phẩm";

    private static final int HOME_PRODUCT_LIMIT = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        initViews();
        showUserName();
        loadHomeUserAvatar();
        setupProductList();
        setupCategoryList();
        setupClickEvents();
        setupBottomNavigation();

        loadCategories();
        loadProducts();
    }

    private void initViews() {
        tvHello = findViewById(R.id.tvHello);
        imgHomeAvatar = findViewById(R.id.imgHomeAvatar);
        btnCart = findViewById(R.id.btnCart);
        rvProducts = findViewById(R.id.rvProducts);
        rvCategories = findViewById(R.id.rvCategories);
        tvProductSectionTitle = findViewById(R.id.tvProductSectionTitle);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        tvViewAllProducts = findViewById(R.id.tvViewAllProducts);

    }


    private void showUserName() {
        String fullName = sessionManager.getFullName();

        if (fullName != null && !fullName.isEmpty()) {
            tvHello.setText("Xin chào, " + fullName + " 👋");
        } else {
            tvHello.setText("Xin chào 👋");
        }
    }

    private void setupProductList() {
        productAdapter = new ProductAdapter(product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupClickEvents() {
        tvViewAllProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            intent.putExtra("category_id", currentCategoryId);
            intent.putExtra("category_name", currentCategoryName);
            startActivity(intent);
        });
        imgHomeAvatar.setOnClickListener(v -> openEditProfile());

        tvHello.setOnClickListener(v -> openEditProfile());
    }
    private void openEditProfile() {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }
    private void loadHomeUserAvatar() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            imgHomeAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            return;
        }

        ApiClient.getApiService().getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    sessionManager.saveUser(user);
                    showUserName();

                    String avatarUrl = user.getAvatarUrl();

                    if (avatarUrl == null || avatarUrl.isEmpty()) {
                        imgHomeAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
                        return;
                    }

                    Glide.with(MainActivity.this)
                            .load(buildImageUrl(avatarUrl))
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .error(android.R.drawable.ic_menu_myplaces)
                            .into(imgHomeAvatar);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                imgHomeAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            }
        });
    }

    private String buildImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }

        if (imageUrl.startsWith("http")) {
            return imageUrl;
        }

        String baseUrl = ApiClient.getBaseUrl();

        if (baseUrl.endsWith("/") && imageUrl.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + imageUrl;
        }

        if (!baseUrl.endsWith("/") && !imageUrl.startsWith("/")) {
            return baseUrl + "/" + imageUrl;
        }

        return baseUrl + imageUrl;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager != null) {
            showUserName();
            loadHomeUserAvatar();
        }
    }

    private void loadProducts() {
        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showHomeProducts(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Không lấy được danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setupCategoryList() {
        categoryAdapter = new CategoryAdapter(category -> {
            if (category.getId() == 0L) {
                currentCategoryId = 0L;
                currentCategoryName = "Tất cả sản phẩm";

                tvProductSectionTitle.setText("Sản phẩm mới");
                loadProducts();
            } else {
                currentCategoryId = category.getId();
                currentCategoryName = category.getName();

                tvProductSectionTitle.setText(category.getName());
                loadProductsByCategory(category.getId());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );

        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setAdapter(categoryAdapter);
    }
    private void loadCategories() {
        ApiClient.getApiService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = new ArrayList<>();

                    categories.add(new Category(0L, "Tất cả"));
                    categories.addAll(response.body());

                    categoryAdapter.setData(categories);
                } else {
                    Toast.makeText(MainActivity.this, "Không lấy được danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi danh mục: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void loadProductsByCategory(Long categoryId) {
        ApiClient.getApiService().getProductsByCategory(categoryId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showHomeProducts(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Không lấy được sản phẩm theo danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi lọc sản phẩm: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void showHomeProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            productAdapter.setData(new ArrayList<>());
            tvViewAllProducts.setVisibility(View.GONE);
            return;
        }

        int endIndex = Math.min(products.size(), HOME_PRODUCT_LIMIT);

        List<Product> homeProducts = new ArrayList<>(products.subList(0, endIndex));
        productAdapter.setData(homeProducts);

        if (products.size() > HOME_PRODUCT_LIMIT) {
            tvViewAllProducts.setVisibility(View.VISIBLE);
        } else {
            tvViewAllProducts.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
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
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }
}