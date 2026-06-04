package com.example.fashionshopmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.activity.ProductDetailActivity;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello, btnCart;
    private RecyclerView rvProducts;
    private BottomNavigationView bottomNavigation;

    private ProductAdapter productAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        initViews();
        showUserName();
        setupProductList();
        setupClickEvents();

        loadProducts();
    }

    private void initViews() {
        tvHello = findViewById(R.id.tvHello);
        btnCart = findViewById(R.id.btnCart);
        rvProducts = findViewById(R.id.rvProducts);
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

    private void setupClickEvents() {
        btnCart.setOnClickListener(v -> {
            Toast.makeText(this, "Mở giỏ hàng sau", Toast.LENGTH_SHORT).show();

            // Sau này khi có CartActivity thì mở như này:
            // Intent intent = new Intent(MainActivity.this, CartActivity.class);
            // startActivity(intent);
        });

        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_orders) {
                Toast.makeText(this, "Mở đơn hàng sau", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_store) {
                Toast.makeText(this, "Mở cửa hàng sau", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Mở tài khoản sau", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void loadProducts() {
        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setData(response.body());
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
}