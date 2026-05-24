package com.example.fashionshopmobile;
import com.example.fashionshopmobile.activity.ProductDetailActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.example.fashionshopmobile.activity.LoginActivity;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerProducts;
    private ProgressBar progressBar;
    private TextView txtMessage;

    private ProductAdapter productAdapter;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> logout());

        recyclerProducts = findViewById(R.id.recyclerProducts);
        progressBar = findViewById(R.id.progressBar);
        txtMessage = findViewById(R.id.txtMessage);

        productAdapter = new ProductAdapter();
        productAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setAdapter(productAdapter);

        loadProducts();
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        txtMessage.setText("");

        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    productAdapter.setData(products);

                    if (products.isEmpty()) {
                        txtMessage.setText("Chưa có sản phẩm nào");
                    }
                } else {
                    txtMessage.setText("Không lấy được danh sách sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                txtMessage.setText("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        sessionManager.logout();

        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}