package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.Category;
import com.example.fashionshopmobile.model.Product;
import com.google.android.material.slider.RangeSlider;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private static final float MIN_PRICE = 0f;
    private static final float MAX_PRICE = 1_000_000f;

    private TextView tvTitle;
    private TextView btnBack;
    private TextView btnToggleFilter;
    private TextView tvPriceRange;
    private TextView tvEmpty;
    private TextView btnApplyFilter;
    private TextView btnResetFilter;

    private RecyclerView rvAllProducts;
    private LinearLayout layoutFilterContent;
    private LinearLayout layoutCategoryCheckboxes;

    private CheckBox cbGenderNam;
    private CheckBox cbGenderNu;
    private CheckBox cbOnlySale;

    private RangeSlider priceSlider;
    private ProductAdapter productAdapter;

    private Long categoryId;
    private String categoryName;
    private String keyword;

    private final List<CheckBox> categoryCheckBoxes = new ArrayList<>();
    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        categoryId = getIntent().getLongExtra("category_id", 0L);
        categoryName = getIntent().getStringExtra("category_name");
        keyword = getIntent().getStringExtra("keyword");

        initViews();
        setupTitle();
        setupRecyclerView();
        setupFilterEvents();

        loadCategoriesForFilter();
        loadSearchProducts();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btnBack);
        btnToggleFilter = findViewById(R.id.btnToggleFilter);
        tvPriceRange = findViewById(R.id.tvPriceRange);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        btnResetFilter = findViewById(R.id.btnResetFilter);

        rvAllProducts = findViewById(R.id.rvAllProducts);
        layoutFilterContent = findViewById(R.id.layoutFilterContent);
        layoutCategoryCheckboxes = findViewById(R.id.layoutCategoryCheckboxes);

        cbGenderNam = findViewById(R.id.cbGenderNam);
        cbGenderNu = findViewById(R.id.cbGenderNu);
        cbOnlySale = findViewById(R.id.cbOnlySale);

        priceSlider = findViewById(R.id.priceSlider);

        priceSlider.setValueFrom(MIN_PRICE);
        priceSlider.setValueTo(MAX_PRICE);
        priceSlider.setStepSize(50_000f);
        priceSlider.setValues(MIN_PRICE, MAX_PRICE);

        updatePriceRangeText();
    }

    private void setupTitle() {
        if (keyword != null && !keyword.trim().isEmpty()) {
            tvTitle.setText("Kết quả: " + keyword.trim());
            return;
        }

        if (categoryName != null && !categoryName.trim().isEmpty()) {
            tvTitle.setText(categoryName);
        } else {
            tvTitle.setText("Tất cả sản phẩm");
        }
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

    private void setupFilterEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnToggleFilter.setOnClickListener(v -> {
            if (layoutFilterContent.getVisibility() == View.VISIBLE) {
                layoutFilterContent.setVisibility(View.GONE);
                btnToggleFilter.setText("☰ Lọc");
            } else {
                layoutFilterContent.setVisibility(View.VISIBLE);
                btnToggleFilter.setText("▲ Thu gọn");
            }
        });

        priceSlider.addOnChangeListener((slider, value, fromUser) -> updatePriceRangeText());

        btnApplyFilter.setOnClickListener(v -> loadSearchProducts());

        btnResetFilter.setOnClickListener(v -> resetFilter());
    }

    private void resetFilter() {
        priceSlider.setValues(MIN_PRICE, MAX_PRICE);

        for (CheckBox checkBox : categoryCheckBoxes) {
            checkBox.setChecked(false);
        }

        if (categoryId != null && categoryId > 0) {
            for (CheckBox checkBox : categoryCheckBoxes) {
                Object tag = checkBox.getTag();

                if (tag instanceof Long && ((Long) tag).equals(categoryId)) {
                    checkBox.setChecked(true);
                    break;
                }
            }
        }

        cbGenderNam.setChecked(false);
        cbGenderNu.setChecked(false);
        cbOnlySale.setChecked(false);

        updatePriceRangeText();
        loadSearchProducts();
    }

    private void updatePriceRangeText() {
        List<Float> values = priceSlider.getValues();

        if (values == null || values.size() < 2) {
            tvPriceRange.setText("0đ - 1.000.000đ");
            return;
        }

        int min = Math.round(values.get(0));
        int max = Math.round(values.get(1));

        tvPriceRange.setText(priceFormatter.format(min) + "đ - " + priceFormatter.format(max) + "đ");
    }

    private void loadCategoriesForFilter() {
        ApiClient.getApiService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showCategoryCheckboxes(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Không lấy được danh mục lọc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategoryCheckboxes(List<Category> categories) {
        layoutCategoryCheckboxes.removeAllViews();
        categoryCheckBoxes.clear();

        for (Category category : categories) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(category.getName());
            checkBox.setTextSize(14);
            checkBox.setTextColor(getResources().getColor(R.color.dark_text, getTheme()));
            checkBox.setTag(category.getId());
            checkBox.setPadding(0, 2, 0, 2);

            if (categoryId != null && categoryId > 0 && categoryId.equals(category.getId())) {
                checkBox.setChecked(true);
            }

            layoutCategoryCheckboxes.addView(checkBox);
            categoryCheckBoxes.add(checkBox);
        }
    }

    private void loadSearchProducts() {
        List<Float> priceValues = priceSlider.getValues();

        BigDecimal minPrice = BigDecimal.valueOf(Math.round(priceValues.get(0)));
        BigDecimal maxPrice = BigDecimal.valueOf(Math.round(priceValues.get(1)));

        List<Long> selectedCategoryIds = getSelectedCategoryIds();
        List<String> selectedGenders = getSelectedGenders();

        Boolean onlySale = cbOnlySale.isChecked() ? true : null;

        ApiClient.getApiService()
                .searchProducts(
                        getKeywordOrNull(),
                        minPrice,
                        maxPrice,
                        selectedCategoryIds.isEmpty() ? null : selectedCategoryIds,
                        selectedGenders.isEmpty() ? null : selectedGenders,
                        onlySale
                )
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showProducts(response.body());
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

    private String getKeywordOrNull() {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        return keyword.trim();
    }

    private List<Long> getSelectedCategoryIds() {
        List<Long> selectedIds = new ArrayList<>();

        for (CheckBox checkBox : categoryCheckBoxes) {
            if (!checkBox.isChecked()) {
                continue;
            }

            Object tag = checkBox.getTag();

            if (tag instanceof Long) {
                selectedIds.add((Long) tag);
            }
        }

        if (selectedIds.isEmpty() && categoryId != null && categoryId > 0) {
            selectedIds.add(categoryId);
        }

        return selectedIds;
    }

    private List<String> getSelectedGenders() {
        List<String> genders = new ArrayList<>();

        if (cbGenderNam.isChecked()) {
            genders.add("MEN");
        }

        if (cbGenderNu.isChecked()) {
            genders.add("WOMEN");
        }

        return genders;
    }

    private void showProducts(List<Product> products) {
        productAdapter.setData(products);

        if (products == null || products.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvAllProducts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvAllProducts.setVisibility(View.VISIBLE);
        }
    }
}