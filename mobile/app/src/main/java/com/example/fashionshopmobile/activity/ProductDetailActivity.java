package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.adapter.ProductVariantAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.ProductVariant;
import com.example.fashionshopmobile.request.AddCartRequest;
import com.example.fashionshopmobile.utils.ImageUrlUtils;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgDetailProduct;

    private TextView btnBack;
    private View btnCartTop;
    private TextView txtCartBadge;

    private TextView txtDetailName;
    private TextView txtDetailCategory;
    private TextView txtDetailPrice;
    private TextView txtOriginalPrice;
    private TextView txtTotalStock;
    private TextView txtDetailBrand;
    private TextView txtDetailGender;
    private TextView txtDetailDescription;
    private TextView txtSelectedVariantStock;
    private TextView txtQuantity;
    private TextView btnDecrease;
    private TextView btnIncrease;
    private TextView txtDetailMessage;
    private TextView txtRelatedMessage;

    private MaterialButton btnAddToCart;
    private MaterialButton btnBuyNow;

    private RecyclerView recyclerVariants;
    private RecyclerView recyclerRelatedProducts;

    private ProductVariantAdapter variantAdapter;
    private ProductAdapter relatedProductAdapter;

    private SessionManager sessionManager;

    private Long productId;
    private Product currentProduct;
    private ProductVariant selectedVariant;

    private int quantity = 1;

    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sessionManager = new SessionManager(this);
        productId = getIntent().getLongExtra("product_id", -1);

        initViews();
        setupVariantRecyclerView();
        setupRelatedProductRecyclerView();
        setupClickEvents();

        if (productId == -1) {
            showMessage("Không nhận được mã sản phẩm");
            return;
        }

        loadProductDetail();
        loadProductVariants();
        loadCartCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
    }

    private void initViews() {
        imgDetailProduct = findViewById(R.id.imgDetailProduct);

        btnBack = findViewById(R.id.btnBack);
        btnCartTop = findViewById(R.id.btnCartTop);
        txtCartBadge = findViewById(R.id.txtCartBadge);

        txtDetailName = findViewById(R.id.txtDetailName);
        txtDetailCategory = findViewById(R.id.txtDetailCategory);
        txtDetailPrice = findViewById(R.id.txtDetailPrice);
        txtOriginalPrice = findViewById(R.id.txtOriginalPrice);
        txtTotalStock = findViewById(R.id.txtTotalStock);
        txtDetailBrand = findViewById(R.id.txtDetailBrand);
        txtDetailGender = findViewById(R.id.txtDetailGender);
        txtDetailDescription = findViewById(R.id.txtDetailDescription);
        txtSelectedVariantStock = findViewById(R.id.txtSelectedVariantStock);
        txtQuantity = findViewById(R.id.txtQuantity);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        txtDetailMessage = findViewById(R.id.txtDetailMessage);
        txtRelatedMessage = findViewById(R.id.txtRelatedMessage);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);

        recyclerVariants = findViewById(R.id.recyclerVariants);
        recyclerRelatedProducts = findViewById(R.id.recyclerRelatedProducts);
    }

    private void setupVariantRecyclerView() {
        variantAdapter = new ProductVariantAdapter(variant -> {
            selectedVariant = variant;

            int stock = getStock(variant);
            quantity = stock > 0 ? 1 : 0;

            updateSelectedVariantInfo();
            updateQuantityView();

            if (variant.getImageUrl() != null && !variant.getImageUrl().trim().isEmpty()) {
                loadProductImage(variant.getImageUrl());
            } else if (currentProduct != null) {
                loadProductImage(currentProduct.getImageUrl());
            }
        });

        recyclerVariants.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        recyclerVariants.setAdapter(variantAdapter);
    }

    private void setupRelatedProductRecyclerView() {
        relatedProductAdapter = new ProductAdapter(product -> {
            Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        recyclerRelatedProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerRelatedProducts.setAdapter(relatedProductAdapter);
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnCartTop.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnDecrease.setOnClickListener(v -> decreaseQuantity());

        btnIncrease.setOnClickListener(v -> increaseQuantity());

        btnAddToCart.setOnClickListener(v -> addToCart());

        btnBuyNow.setOnClickListener(v -> {
            if (isValidBeforeAction()) {
                Toast.makeText(this, "Mua ngay sẽ làm ở module thanh toán", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetail() {
        ApiClient.getApiService().getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProduct = response.body();
                    showProduct(currentProduct);
                    loadRelatedProducts(currentProduct.getCategoryId());
                } else {
                    showMessage("Không lấy được chi tiết sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                showMessage("Lỗi API chi tiết: " + t.getMessage());
            }
        });
    }

    private void loadProductVariants() {
        ApiClient.getApiService().getProductVariants(productId).enqueue(new Callback<List<ProductVariant>>() {
            @Override
            public void onResponse(Call<List<ProductVariant>> call, Response<List<ProductVariant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductVariant> variants = response.body();

                    variantAdapter.setData(variants);
                    updateTotalStock(variants);

                    if (variants.isEmpty()) {
                        txtSelectedVariantStock.setText("Sản phẩm chưa có phân loại");
                        btnAddToCart.setEnabled(false);
                        btnBuyNow.setEnabled(false);
                    } else {
                        txtSelectedVariantStock.setText("Vui lòng chọn phân loại sản phẩm");
                    }
                } else {
                    showMessage("Không lấy được danh sách size/màu");
                }
            }

            @Override
            public void onFailure(Call<List<ProductVariant>> call, Throwable t) {
                showMessage("Lỗi API variant: " + t.getMessage());
            }
        });
    }

    private void loadRelatedProducts(Long categoryId) {
        if (categoryId == null) {
            txtRelatedMessage.setText("Không xác định được danh mục sản phẩm");
            return;
        }

        ApiClient.getApiService().getProductsByCategory(categoryId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> relatedProducts = filterRelatedProducts(response.body());

                    relatedProductAdapter.setData(relatedProducts);

                    if (relatedProducts.isEmpty()) {
                        txtRelatedMessage.setText("Chưa có sản phẩm cùng danh mục");
                    } else {
                        txtRelatedMessage.setText("");
                    }
                } else {
                    txtRelatedMessage.setText("Không lấy được sản phẩm cùng danh mục");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                txtRelatedMessage.setText("Lỗi API sản phẩm liên quan: " + t.getMessage());
            }
        });
    }

    private List<Product> filterRelatedProducts(List<Product> products) {
        List<Product> result = new ArrayList<>();

        for (Product product : products) {
            if (product.getId() == null) {
                continue;
            }

            if (product.getId().equals(productId)) {
                continue;
            }

            result.add(product);

            if (result.size() == 4) {
                break;
            }
        }

        return result;
    }

    private void showProduct(Product product) {
        txtDetailName.setText(getTextOrDefault(product.getName(), "Tên sản phẩm"));
        txtDetailCategory.setText("Danh mục: " + getTextOrDefault(product.getCategoryName(), "Chưa có"));

        showPrice(product);

        txtDetailBrand.setText("Thương hiệu: " + getTextOrDefault(product.getBrand(), "Chưa có"));
        txtDetailGender.setText("Giới tính: " + getTextOrDefault(product.getGender(), "Chưa có"));
        txtDetailDescription.setText(getTextOrDefault(product.getDescription(), "Chưa có mô tả"));

        loadProductImage(product.getImageUrl());
    }

    private void showPrice(Product product) {
        BigDecimal price = product.getPrice();
        BigDecimal salePrice = product.getSalePrice();

        if (salePrice != null) {
            txtDetailPrice.setText(formatPrice(salePrice));

            if (price != null) {
                txtOriginalPrice.setVisibility(View.VISIBLE);
                txtOriginalPrice.setText(formatPrice(price));
                txtOriginalPrice.setPaintFlags(
                        txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                );
            } else {
                txtOriginalPrice.setVisibility(View.GONE);
            }

            return;
        }

        if (price != null) {
            txtDetailPrice.setText(formatPrice(price));
        } else {
            txtDetailPrice.setText("Liên hệ");
        }

        txtOriginalPrice.setVisibility(View.GONE);
    }

    private void updateTotalStock(List<ProductVariant> variants) {
        int totalStock = 0;

        for (ProductVariant variant : variants) {
            totalStock += getStock(variant);
        }

        if (totalStock > 0) {
            txtTotalStock.setText("Tổng tồn kho: " + totalStock + " sản phẩm");
        } else {
            txtTotalStock.setText("Tổng tồn kho: Hết hàng");
        }
    }

    private void updateSelectedVariantInfo() {
        if (selectedVariant == null) {
            txtSelectedVariantStock.setText("Vui lòng chọn phân loại sản phẩm");
            return;
        }

        String color = getTextOrDefault(selectedVariant.getColor(), "");
        String size = getTextOrDefault(selectedVariant.getSize(), "");
        int stock = getStock(selectedVariant);

        if (stock > 0) {
            txtSelectedVariantStock.setText(
                    "Đã chọn: " + color + " · " + size + " - Còn " + stock + " sản phẩm"
            );
        } else {
            txtSelectedVariantStock.setText(
                    "Đã chọn: " + color + " · " + size + " - Hết hàng"
            );
        }
    }

    private void loadProductImage(String imageUrl) {
        String fullImageUrl = ImageUrlUtils.getFullImageUrl(imageUrl);

        Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgDetailProduct);
    }

    private void decreaseQuantity() {
        if (selectedVariant == null) {
            Toast.makeText(this, "Vui lòng chọn phân loại sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity > 1) {
            quantity--;
            updateQuantityView();
        }
    }

    private void increaseQuantity() {
        if (selectedVariant == null) {
            Toast.makeText(this, "Vui lòng chọn phân loại sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock = getStock(selectedVariant);

        if (stock <= 0) {
            Toast.makeText(this, "Phân loại này đã hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity >= stock) {
            Toast.makeText(this, "Số lượng đã đạt tối đa trong kho", Toast.LENGTH_SHORT).show();
            return;
        }

        quantity++;
        updateQuantityView();
    }

    private void updateQuantityView() {
        txtQuantity.setText(String.valueOf(quantity));

        boolean canBuy = selectedVariant != null && getStock(selectedVariant) > 0 && quantity > 0;

        btnAddToCart.setEnabled(canBuy);
        btnBuyNow.setEnabled(canBuy);

        btnDecrease.setEnabled(canBuy && quantity > 1);
        btnIncrease.setEnabled(canBuy && quantity < getStock(selectedVariant));
    }

    private boolean isValidBeforeAction() {
        if (selectedVariant == null) {
            Toast.makeText(this, "Vui lòng chọn phân loại sản phẩm", Toast.LENGTH_SHORT).show();
            return false;
        }

        int stock = getStock(selectedVariant);

        if (stock <= 0) {
            Toast.makeText(this, "Phân loại này đã hết hàng", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (quantity < 1 || quantity > stock) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addToCart() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        if (!isValidBeforeAction()) {
            return;
        }

        AddCartRequest request = new AddCartRequest(
                userId,
                productId,
                selectedVariant.getId(),
                quantity
        );

        btnAddToCart.setEnabled(false);
        btnAddToCart.setText("Đang thêm...");

        ApiClient.getApiService().addToCart(request).enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                btnAddToCart.setText("Thêm vào giỏ");
                updateQuantityView();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(
                            ProductDetailActivity.this,
                            "Đã thêm sản phẩm vào giỏ hàng",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadCartCount();
                } else {
                    Toast.makeText(
                            ProductDetailActivity.this,
                            "Thêm vào giỏ thất bại. Vui lòng kiểm tra tồn kho",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                btnAddToCart.setText("Thêm vào giỏ");
                updateQuantityView();

                Toast.makeText(
                        ProductDetailActivity.this,
                        "Không kết nối được backend: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
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

    private int getStock(ProductVariant variant) {
        if (variant == null || variant.getQuantity() == null) {
            return 0;
        }

        return variant.getQuantity();
    }

    private String formatPrice(BigDecimal price) {
        return priceFormatter.format(price) + " đ";
    }

    private String getTextOrDefault(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    private void showMessage(String message) {
        txtDetailMessage.setText(message);
    }
}