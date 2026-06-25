package com.example.fashionshopmobile.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.adapter.ProductReviewAdapter;
import com.example.fashionshopmobile.adapter.ProductVariantAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.ProductReview;
import com.example.fashionshopmobile.model.ProductVariant;
import com.example.fashionshopmobile.model.ReviewEligibility;
import com.example.fashionshopmobile.model.ReviewableOrderItem;
import com.example.fashionshopmobile.request.AddCartRequest;
import com.example.fashionshopmobile.request.CreateProductReviewRequest;
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

    private TextView txtReviewSummary;
    private TextView txtReviewEligibility;
    private TextView txtReviewMessage;

    private MaterialButton btnWriteReview;
    private MaterialButton btnAddToCart;

    private RecyclerView recyclerVariants;
    private RecyclerView recyclerRelatedProducts;
    private RecyclerView recyclerReviews;

    private ProductVariantAdapter variantAdapter;
    private ProductAdapter relatedProductAdapter;
    private ProductReviewAdapter productReviewAdapter;

    private SessionManager sessionManager;

    private Long productId;
    private Product currentProduct;
    private ProductVariant selectedVariant;
    private ReviewEligibility currentReviewEligibility;

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
        setupReviewRecyclerView();
        setupClickEvents();

        if (productId == -1) {
            showMessage("Không nhận được mã sản phẩm");
            return;
        }

        loadProductDetail();
        loadProductVariants();
        loadProductReviews();
        loadReviewEligibility();
        loadCartCount();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadCartCount();

        if (productId != null && productId != -1) {
            loadReviewEligibility();
        }
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

        recyclerVariants = findViewById(R.id.recyclerVariants);
        recyclerRelatedProducts = findViewById(R.id.recyclerRelatedProducts);

        txtReviewSummary = findViewById(R.id.txtReviewSummary);
        txtReviewEligibility = findViewById(R.id.txtReviewEligibility);
        txtReviewMessage = findViewById(R.id.txtReviewMessage);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        recyclerReviews = findViewById(R.id.recyclerReviews);
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

    private void setupReviewRecyclerView() {
        productReviewAdapter = new ProductReviewAdapter();
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(productReviewAdapter);
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



        btnWriteReview.setOnClickListener(v -> showReviewDialog());
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

    private void loadProductReviews() {
        if (productId == null || productId == -1) {
            return;
        }

        ApiClient.getApiService().getProductReviews(productId).enqueue(new Callback<List<ProductReview>>() {
            @Override
            public void onResponse(Call<List<ProductReview>> call, Response<List<ProductReview>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductReview> reviews = response.body();
                    productReviewAdapter.setData(reviews);

                    if (reviews.isEmpty()) {
                        txtReviewMessage.setText("Chưa có đánh giá nào cho sản phẩm này");
                    } else {
                        txtReviewMessage.setText("");
                    }
                } else {
                    txtReviewMessage.setText("Không lấy được danh sách đánh giá");
                }
            }

            @Override
            public void onFailure(Call<List<ProductReview>> call, Throwable t) {
                txtReviewMessage.setText("Lỗi API đánh giá: " + t.getMessage());
            }
        });
    }

    private void loadReviewEligibility() {
        Long userId = sessionManager.getUserId();

        if (productId == null || productId == -1) {
            return;
        }

        if (userId == null) {
            currentReviewEligibility = null;
            txtReviewSummary.setText("Chưa có đánh giá");
            txtReviewEligibility.setText("Đăng nhập và mua hàng thành công để đánh giá sản phẩm");
            btnWriteReview.setVisibility(View.GONE);
            return;
        }

        ApiClient.getApiService().getReviewEligibility(productId, userId).enqueue(new Callback<ReviewEligibility>() {
            @Override
            public void onResponse(Call<ReviewEligibility> call, Response<ReviewEligibility> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentReviewEligibility = response.body();
                    updateReviewEligibilityView(currentReviewEligibility);
                } else {
                    currentReviewEligibility = null;
                    txtReviewEligibility.setText("Không kiểm tra được quyền đánh giá");
                    btnWriteReview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ReviewEligibility> call, Throwable t) {
                currentReviewEligibility = null;
                txtReviewEligibility.setText("Lỗi kiểm tra quyền đánh giá: " + t.getMessage());
                btnWriteReview.setVisibility(View.GONE);
            }
        });
    }

    private void updateReviewEligibilityView(ReviewEligibility eligibility) {
        double avg = eligibility.getAverageRating() == null ? 0 : eligibility.getAverageRating();
        long count = eligibility.getReviewCount() == null ? 0 : eligibility.getReviewCount();

        if (count > 0) {
            txtReviewSummary.setText(String.format(Locale.getDefault(), "%.1f ★ (%d)", avg, count));
        } else {
            txtReviewSummary.setText("Chưa có đánh giá");
        }

        if (eligibility.isCanReview()) {
            txtReviewEligibility.setText(
                    "Bạn còn " + eligibility.getAvailableReviewCount() + " lượt đánh giá cho sản phẩm này"
            );
            btnWriteReview.setVisibility(View.VISIBLE);
        } else {
            txtReviewEligibility.setText(
                    "Bạn chỉ được đánh giá khi đã mua sản phẩm và đơn hàng đã thành công"
            );
            btnWriteReview.setVisibility(View.GONE);
        }
    }

    private void showReviewDialog() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
            return;
        }

        if (currentReviewEligibility == null
                || currentReviewEligibility.getReviewableOrderItems() == null
                || currentReviewEligibility.getReviewableOrderItems().isEmpty()) {
            Toast.makeText(this, "Bạn chưa có lượt mua hợp lệ để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        List<ReviewableOrderItem> reviewableItems = currentReviewEligibility.getReviewableOrderItems();
        List<String> labels = new ArrayList<>();

        for (ReviewableOrderItem item : reviewableItems) {
            labels.add(buildReviewableItemLabel(item));
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(20);
        layout.setPadding(padding, padding, padding, 0);

        TextView txtChooseOrderItem = new TextView(this);
        txtChooseOrderItem.setText("Chọn lượt mua muốn đánh giá");
        txtChooseOrderItem.setTextSize(14);
        layout.addView(txtChooseOrderItem);

        Spinner spinnerOrderItem = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                labels
        );
        spinnerOrderItem.setAdapter(spinnerAdapter);
        layout.addView(spinnerOrderItem);

        TextView txtRatingLabel = new TextView(this);
        txtRatingLabel.setText("Số sao");
        txtRatingLabel.setTextSize(14);
        txtRatingLabel.setPadding(0, dpToPx(12), 0, 0);
        layout.addView(txtRatingLabel);

        final int[] selectedRating = {5};

        LinearLayout starLayout = new LinearLayout(this);
        starLayout.setOrientation(LinearLayout.HORIZONTAL);
        starLayout.setPadding(0, dpToPx(6), 0, dpToPx(6));

        List<TextView> starViews = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            TextView star = new TextView(this);
            star.setText("★");
            star.setTextSize(30);
            star.setPadding(dpToPx(4), 0, dpToPx(4), 0);

            int ratingValue = i;

            star.setOnClickListener(v -> {
                selectedRating[0] = ratingValue;
                updateStarViews(starViews, selectedRating[0]);
            });

            starViews.add(star);
            starLayout.addView(star);
        }

        layout.addView(starLayout);
        updateStarViews(starViews, selectedRating[0]);

        EditText edtComment = new EditText(this);
        edtComment.setHint("Nhập nội dung đánh giá");
        edtComment.setMinLines(3);
        edtComment.setMaxLines(5);
        edtComment.setSingleLine(false);
        layout.addView(edtComment);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Đánh giá sản phẩm")
                .setView(layout)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Gửi đánh giá", null)
                .create();

        dialog.setOnShowListener(dialogInterface ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    int selectedPosition = spinnerOrderItem.getSelectedItemPosition();

                    if (selectedPosition < 0 || selectedPosition >= reviewableItems.size()) {
                        Toast.makeText(this, "Vui lòng chọn lượt mua", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ReviewableOrderItem selectedItem = reviewableItems.get(selectedPosition);
                    int rating = selectedRating[0];
                    String comment = edtComment.getText().toString().trim();

                    if (rating < 1 || rating > 5) {
                        Toast.makeText(this, "Vui lòng chọn số sao từ 1 đến 5", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    submitReview(dialog, userId, selectedItem.getOrderItemId(), rating, comment);
                })
        );

        dialog.show();
    }

    private void submitReview(AlertDialog dialog, Long userId, Long orderItemId, int rating, String comment) {
        CreateProductReviewRequest request = new CreateProductReviewRequest(
                userId,
                productId,
                orderItemId,
                rating,
                comment
        );

        ApiClient.getApiService().createProductReview(request).enqueue(new Callback<ProductReview>() {
            @Override
            public void onResponse(Call<ProductReview> call, Response<ProductReview> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    loadProductReviews();
                    loadReviewEligibility();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Gửi đánh giá thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductReview> call, Throwable t) {
                Toast.makeText(
                        ProductDetailActivity.this,
                        "Lỗi gửi đánh giá: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private String buildReviewableItemLabel(ReviewableOrderItem item) {
        StringBuilder builder = new StringBuilder();

        builder.append("Đơn #").append(item.getOrderId() == null ? "" : item.getOrderId());

        if (item.getColor() != null && !item.getColor().trim().isEmpty()) {
            builder.append(" - ").append(item.getColor());
        }

        if (item.getSize() != null && !item.getSize().trim().isEmpty()) {
            builder.append(" / ").append(item.getSize());
        }

        if (item.getQuantity() != null) {
            builder.append(" - SL: ").append(item.getQuantity());
        }

        if (item.getOrderCreatedAt() != null && item.getOrderCreatedAt().length() >= 10) {
            builder.append(" - ").append(item.getOrderCreatedAt().substring(0, 10));
        }

        return builder.toString();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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
    private void updateStarViews(List<TextView> starViews, int selectedRating) {
        for (int i = 0; i < starViews.size(); i++) {
            TextView star = starViews.get(i);

            if (i < selectedRating) {
                star.setText("★");
            } else {
                star.setText("☆");
            }
        }
    }
}