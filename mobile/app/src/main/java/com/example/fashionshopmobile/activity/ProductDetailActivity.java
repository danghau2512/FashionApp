package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.ProductVariantAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.ProductVariant;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView txtDetailName;
    private TextView txtDetailCategory;
    private TextView txtDetailPrice;
    private TextView txtDetailBrand;
    private TextView txtDetailGender;
    private TextView txtDetailDescription;
    private TextView txtDetailMessage;

    private RecyclerView recyclerVariants;
    private ProductVariantAdapter variantAdapter;

    private Long productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getLongExtra("product_id", -1);

        txtDetailName = findViewById(R.id.txtDetailName);
        txtDetailCategory = findViewById(R.id.txtDetailCategory);
        txtDetailPrice = findViewById(R.id.txtDetailPrice);
        txtDetailBrand = findViewById(R.id.txtDetailBrand);
        txtDetailGender = findViewById(R.id.txtDetailGender);
        txtDetailDescription = findViewById(R.id.txtDetailDescription);
        txtDetailMessage = findViewById(R.id.txtDetailMessage);
        recyclerVariants = findViewById(R.id.recyclerVariants);

        variantAdapter = new ProductVariantAdapter();

        recyclerVariants.setLayoutManager(new LinearLayoutManager(this));
        recyclerVariants.setAdapter(variantAdapter);

        if (productId == -1) {
            txtDetailMessage.setText("Không nhận được mã sản phẩm");
            return;
        }

        loadProductDetail();
        loadProductVariants();
    }

    private void loadProductDetail() {
        ApiClient.getApiService().getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showProduct(response.body());
                } else {
                    txtDetailMessage.setText("Không lấy được chi tiết sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                txtDetailMessage.setText("Lỗi API chi tiết: " + t.getMessage());
            }
        });
    }

    private void loadProductVariants() {
        ApiClient.getApiService().getProductVariants(productId).enqueue (new Callback<List<ProductVariant>>() {
            @Override
            public void onResponse(Call<List<ProductVariant>> call, Response<List<ProductVariant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    variantAdapter.setData(response.body());
                } else {
                    txtDetailMessage.setText("Không lấy được danh sách size/màu");
                }
            }

            @Override
            public void onFailure(Call<List<ProductVariant>> call, Throwable t) {
                txtDetailMessage.setText("Lỗi API variant: " + t.getMessage());
            }
        });
    }

    private void showProduct(Product product) {
        txtDetailName.setText(product.getName());
        txtDetailCategory.setText("Danh mục: " + product.getCategoryName());

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        if (product.getSalePrice() != null) {
            txtDetailPrice.setText(formatter.format(product.getSalePrice()) + " đ");
        } else {
            txtDetailPrice.setText(formatter.format(product.getPrice()) + " đ");
        }

        txtDetailBrand.setText("Thương hiệu: " + product.getBrand());
        txtDetailGender.setText("Giới tính: " + product.getGender());
        txtDetailDescription.setText(product.getDescription());
    }
}