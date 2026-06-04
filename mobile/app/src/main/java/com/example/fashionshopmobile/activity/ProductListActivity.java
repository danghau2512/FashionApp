package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private TextView tvTitle, btnBack;
    private RecyclerView rvAllProducts;

    private ProductAdapter productAdapter;

    private Long categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        categoryId = getIntent().getLongExtra("category_id", 0L);
        categoryName = getIntent().getStringExtra("category_name");

        initViews();
        setupRecyclerView();
        loadData();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btnBack);
        rvAllProducts = findViewById(R.id.rvAllProducts);

        if (categoryName != null && !categoryName.isEmpty()) {
            tvTitle.setText(categoryName);
        } else {
            tvTitle.setText("Tất cả sản phẩm");
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(product -> {
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        rvAllProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvAllProducts.setAdapter(productAdapter);
    }

    private void loadData() {
        if (categoryId == 0L) {
            loadAllProducts();
        } else {
            loadProductsByCategory();
        }
    }

    private void loadAllProducts() {
        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setData(response.body());
                } else {
                    Toast.makeText(ProductListActivity.this, "Không lấy được sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadProductsByCategory() {
        ApiClient.getApiService().getProductsByCategory(categoryId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setData(response.body());
                } else {
                    Toast.makeText(ProductListActivity.this, "Không lấy được sản phẩm theo danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}