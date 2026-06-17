package com.example.fashionshopmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.activity.CartActivity;
import com.example.fashionshopmobile.activity.ProductDetailActivity;
import com.example.fashionshopmobile.activity.ProductListActivity;
import com.example.fashionshopmobile.adapter.CategoryAdapter;
import com.example.fashionshopmobile.activity.StoreMapActivity;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.model.Category;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello;
    private View btnCart;
    private TextView txtCartBadge;
    private TextView tvProductSectionTitle;
    private TextView tvViewAllProducts;

    private RecyclerView rvProducts;
    private RecyclerView rvCategories;
    private BottomNavigationView bottomNavigation;

    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private SessionManager sessionManager;

    private Long currentCategoryId = 0L;
    private String currentCategoryName = "Tất cả sản phẩm";

    private static final int HOME_PRODUCT_LIMIT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        initViews();
        showUserName();
        setupProductList();
        setupCategoryList();
        setupClickEvents();
        setupBottomNavigation();

        loadCategories();
        loadProducts();
        loadCartCount();
    }



    private void initViews() {
        tvHello = findViewById(R.id.tvHello);
        btnCart = findViewById(R.id.btnCart);
        txtCartBadge = findViewById(R.id.txtCartBadge);

        rvProducts = findViewById(R.id.rvProducts);
        rvCategories = findViewById(R.id.rvCategories);

        tvProductSectionTitle = findViewById(R.id.tvProductSectionTitle);
        tvViewAllProducts = findViewById(R.id.tvViewAllProducts);

        bottomNavigation = findViewById(R.id.bottomNavigation);
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
    private void setupBottomNavigation() {
        // Đánh dấu tab Trang chủ đang được chọn
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Đang ở trang chủ nên không mở Activity mới
            if (itemId == R.id.nav_home) {
                return true;
            }

            // Chức năng đơn hàng chưa hoàn thành
            if (itemId == R.id.nav_orders) {
                Toast.makeText(
                        MainActivity.this,
                        "Chức năng đơn hàng đang được phát triển",
                        Toast.LENGTH_SHORT
                ).show();

                return false;
            }

            // Mở màn hình bản đồ cửa hàng
            if (itemId == R.id.nav_store) {
                Intent intent = new Intent(
                        MainActivity.this,
                        StoreMapActivity.class
                );

                startActivity(intent);
                return true;
            }

            // Chức năng tài khoản do thành viên khác thực hiện
            if (itemId == R.id.nav_profile) {
                Toast.makeText(
                        MainActivity.this,
                        "Chức năng tài khoản đang được phát triển",
                        Toast.LENGTH_SHORT
                ).show();

                return false;
            }

            return false;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        loadCartCount();

        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupCategoryList() {
        categoryAdapter = new CategoryAdapter(category -> {
            if (Long.valueOf(0L).equals(category.getId())) {
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

    private void setupClickEvents() {
        tvViewAllProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            intent.putExtra("category_id", currentCategoryId);
            intent.putExtra("category_name", currentCategoryName);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });
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

    private void loadCartCount() {
        Long userId = sessionManager.getUserId();

        if (txtCartBadge == null) {
            return;
        }

        if (userId == null) {
            txtCartBadge.setVisibility(View.GONE);
            return;
        }

        ApiClient.getApiService().getCartByUserId(userId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int totalQuantity = 0;

                    for (CartItem item : response.body()) {
                        if (item.getQuantity() != null) {
                            totalQuantity += item.getQuantity();
                        }
                    }

                    showCartBadge(totalQuantity);
                } else {
                    txtCartBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                txtCartBadge.setVisibility(View.GONE);
            }
        });
    }

    private void showCartBadge(int totalQuantity) {
        if (txtCartBadge == null) {
            return;
        }

        if (totalQuantity <= 0) {
            txtCartBadge.setVisibility(View.GONE);
            return;
        }

        txtCartBadge.setVisibility(View.VISIBLE);

        if (totalQuantity > 99) {
            txtCartBadge.setText("99+");
        } else {
            txtCartBadge.setText(String.valueOf(totalQuantity));
        }
    }
}