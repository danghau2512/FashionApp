package com.example.fashionshopmobile.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.activity.CartActivity;
import com.example.fashionshopmobile.activity.EditProfileActivity;
import com.example.fashionshopmobile.activity.ProductDetailActivity;
import com.example.fashionshopmobile.activity.ProductListActivity;
import com.example.fashionshopmobile.adapter.CategoryAdapter;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.model.Category;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.utils.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView tvHello;
    private View btnCart;
    private TextView txtCartBadge;
    private TextView tvProductSectionTitle;
    private TextView tvViewAllProducts;
    private TextView btnSearchIcon;
    private EditText edtSearch;
    private ImageView imgHomeAvatar;
    private LinearLayout layoutSearchSuggestions;

    private ImageView imgCategoryBanner;
    private TextView tvBannerTitle;
    private TextView tvBannerDesc;
    private TextView tvBannerButton;

    private RecyclerView rvProducts;
    private RecyclerView rvCategories;

    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private SessionManager sessionManager;

    private Long currentCategoryId = 0L;
    private String currentCategoryName = "Tất cả sản phẩm";

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Call<List<Product>> searchSuggestionCall;
    private String lastSuggestionKeyword = "";

    private static final int HOME_PRODUCT_LIMIT = 4;
    private static final int SEARCH_SUGGESTION_LIMIT = 5;



    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        showUserName();
        loadHomeUserAvatar();
        setupProductList();
        setupCategoryList();
        setupClickEvents();
        setupSearchSuggestions();

        loadCategories();
        loadProducts();
        loadCartCount();

        return view;
    }

    private void initViews(View view) {
        tvHello = view.findViewById(R.id.tvHello);
        btnCart = view.findViewById(R.id.btnCart);
        txtCartBadge = view.findViewById(R.id.txtCartBadge);
        imgHomeAvatar = view.findViewById(R.id.imgHomeAvatar);

        imgCategoryBanner = view.findViewById(R.id.imgCategoryBanner);
        tvBannerTitle = view.findViewById(R.id.tvBannerTitle);
        tvBannerDesc = view.findViewById(R.id.tvBannerDesc);
        tvBannerButton = view.findViewById(R.id.tvBannerButton);

        rvProducts = view.findViewById(R.id.rvProducts);
        rvCategories = view.findViewById(R.id.rvCategories);

        tvProductSectionTitle = view.findViewById(R.id.tvProductSectionTitle);
        tvViewAllProducts = view.findViewById(R.id.tvViewAllProducts);

        btnSearchIcon = view.findViewById(R.id.btnSearchIcon);
        edtSearch = view.findViewById(R.id.edtSearch);
        layoutSearchSuggestions = view.findViewById(R.id.layoutSearchSuggestions);
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
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupCategoryList() {
        categoryAdapter = new CategoryAdapter(category -> {
            if (Long.valueOf(0L).equals(category.getId())) {
                currentCategoryId = 0L;
                currentCategoryName = "Tất cả sản phẩm";

                showDefaultBanner();

                tvProductSectionTitle.setText("Sản phẩm mới");
                loadProducts();
            } else {
                currentCategoryId = category.getId();
                currentCategoryName = category.getName();

                showCategoryBanner(category);

                tvProductSectionTitle.setText(category.getName());
                loadProductsByCategory(category.getId());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setAdapter(categoryAdapter);
    }


    private void showDefaultBanner() {
        if (imgCategoryBanner != null) {
            imgCategoryBanner.setVisibility(View.GONE);
        }

        tvBannerTitle.setText("SALE HÈ 2026");
        tvBannerDesc.setText("Giảm đến 50% cho bộ sưu tập mới");
        tvBannerButton.setText("Mua ngay");
    }

    private void showCategoryBanner(Category category) {
        tvBannerTitle.setText(category.getName());

        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            tvBannerDesc.setText(category.getDescription());
        } else {
            tvBannerDesc.setText("Khám phá sản phẩm thuộc danh mục này");
        }

        tvBannerButton.setText("Xem ngay");

        String imageUrl = category.getImageUrl();

        if (imageUrl == null || imageUrl.isEmpty()) {
            imgCategoryBanner.setVisibility(View.GONE);
            return;
        }

        imgCategoryBanner.setVisibility(View.VISIBLE);

        Glide.with(HomeFragment.this)
                .load(buildImageUrl(imageUrl))
                .placeholder(R.drawable.bg_banner)
                .error(R.drawable.bg_banner)
                .into(imgCategoryBanner);
    }
    private void setupClickEvents() {
        tvViewAllProducts.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProductListActivity.class);
            intent.putExtra("category_id", currentCategoryId);
            intent.putExtra("category_name", currentCategoryName);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CartActivity.class);
            startActivity(intent);
        });

        if (imgHomeAvatar != null) {
            imgHomeAvatar.setOnClickListener(v -> openEditProfile());
        }

        tvHello.setOnClickListener(v -> openEditProfile());

        btnSearchIcon.setOnClickListener(v -> openSearchResult());

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            boolean isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH;
            boolean isEnterKey = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP;

            if (isSearchAction || isEnterKey) {
                openSearchResult();
                return true;
            }

            return false;
        });
    }

    private void setupSearchSuggestions() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                String keyword = text == null ? "" : text.toString().trim();

                searchHandler.removeCallbacksAndMessages(null);

                if (keyword.isEmpty()) {
                    hideSearchSuggestions();
                    cancelSuggestionCall();
                    return;
                }

                searchHandler.postDelayed(() -> loadSearchSuggestions(keyword), 350);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void loadSearchSuggestions(String keyword) {
        if (!isAdded()) {
            return;
        }

        lastSuggestionKeyword = keyword;
        cancelSuggestionCall();

        searchSuggestionCall = ApiClient.getApiService()
                .searchProducts(keyword, null, null, null, null, null);

        searchSuggestionCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) {
                    return;
                }

                String currentKeyword = edtSearch.getText() == null
                        ? ""
                        : edtSearch.getText().toString().trim();

                if (!currentKeyword.equals(lastSuggestionKeyword)) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    showSearchSuggestions(response.body());
                } else {
                    hideSearchSuggestions();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }

                hideSearchSuggestions();
            }
        });
    }

    private void showSearchSuggestions(List<Product> products) {
        layoutSearchSuggestions.removeAllViews();

        if (products == null || products.isEmpty()) {
            hideSearchSuggestions();
            return;
        }

        int count = Math.min(products.size(), SEARCH_SUGGESTION_LIMIT);

        for (int i = 0; i < count; i++) {
            Product product = products.get(i);

            LinearLayout itemLayout = new LinearLayout(requireContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setGravity(Gravity.CENTER_VERTICAL);
            itemLayout.setPadding(dpToPx(10), dpToPx(8), dpToPx(10), dpToPx(8));
            itemLayout.setBackgroundColor(getResources().getColor(R.color.white, requireContext().getTheme()));

            ImageView imgProduct = new ImageView(requireContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    dpToPx(56),
                    dpToPx(56)
            );
            imgProduct.setLayoutParams(imageParams);
            imgProduct.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(HomeFragment.this)
                        .load(buildImageUrl(product.getImageUrl()))
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(imgProduct);
            }

            LinearLayout textLayout = new LinearLayout(requireContext());
            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setPadding(dpToPx(10), 0, 0, 0);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            textLayout.setLayoutParams(textParams);

            TextView tvName = new TextView(requireContext());
            tvName.setText(product.getName());
            tvName.setTextColor(getResources().getColor(R.color.dark_text, requireContext().getTheme()));
            tvName.setTextSize(13);
            tvName.setMaxLines(2);

            TextView tvPrice = new TextView(requireContext());
            tvPrice.setText(formatProductPrice(product));
            tvPrice.setTextColor(getResources().getColor(R.color.primary_pink, requireContext().getTheme()));
            tvPrice.setTextSize(13);
            tvPrice.setPadding(0, dpToPx(3), 0, 0);

            textLayout.addView(tvName);
            textLayout.addView(tvPrice);

            itemLayout.addView(imgProduct);
            itemLayout.addView(textLayout);

            itemLayout.setOnClickListener(v -> openProductDetailFromSuggestion(product));

            layoutSearchSuggestions.addView(itemLayout);

            if (i < count - 1) {
                View divider = new View(requireContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );
                divider.setLayoutParams(params);
                divider.setBackgroundColor(0xFFEFEFEF);
                layoutSearchSuggestions.addView(divider);
            }
        }

        layoutSearchSuggestions.setVisibility(View.VISIBLE);
    }

    private void hideSearchSuggestions() {
        if (layoutSearchSuggestions != null) {
            layoutSearchSuggestions.setVisibility(View.GONE);
            layoutSearchSuggestions.removeAllViews();
        }
    }

    private void cancelSuggestionCall() {
        if (searchSuggestionCall != null && !searchSuggestionCall.isCanceled()) {
            searchSuggestionCall.cancel();
        }
    }

    private String formatProductPrice(Product product) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        if (product.getSalePrice() != null) {
            return formatter.format(product.getSalePrice()) + "đ";
        }

        if (product.getPrice() != null) {
            return formatter.format(product.getPrice()) + "đ";
        }

        return "Liên hệ";
    }

    private void openProductDetailFromSuggestion(Product product) {
        hideKeyboard();
        hideSearchSuggestions();

        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    private void openSearchResult() {
        String keyword = edtSearch.getText() == null ? "" : edtSearch.getText().toString().trim();

        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "Nhập tên sản phẩm cần tìm", Toast.LENGTH_SHORT).show();
            return;
        }

        hideKeyboard();
        hideSearchSuggestions();

        Intent intent = new Intent(requireContext(), ProductListActivity.class);
        intent.putExtra("keyword", keyword);
        intent.putExtra("category_id", 0L);
        intent.putExtra("category_name", "Kết quả tìm kiếm");
        startActivity(intent);
    }

    private void hideKeyboard() {
        View currentView = requireActivity().getCurrentFocus();

        if (currentView == null) {
            currentView = edtSearch;
        }

        InputMethodManager inputMethodManager =
                (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    private void openEditProfile() {
        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void loadHomeUserAvatar() {
        Long userId = sessionManager.getUserId();

        if (imgHomeAvatar == null || userId == null) {
            if (imgHomeAvatar != null) {
                imgHomeAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            }
            return;
        }

        ApiClient.getApiService().getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    sessionManager.saveUser(user);
                    showUserName();

                    String avatarUrl = user.getAvatarUrl();

                    if (avatarUrl == null || avatarUrl.isEmpty()) {
                        imgHomeAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
                        return;
                    }

                    Glide.with(HomeFragment.this)
                            .load(buildImageUrl(avatarUrl))
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .error(android.R.drawable.ic_menu_myplaces)
                            .into(imgHomeAvatar);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (imgHomeAvatar != null) {
                    imgHomeAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
                }
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
    public void onResume() {
        super.onResume();

        if (sessionManager != null) {
            showUserName();
            loadHomeUserAvatar();
            loadCartCount();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        searchHandler.removeCallbacksAndMessages(null);
        cancelSuggestionCall();
    }

    private void loadCategories() {
        ApiClient.getApiService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = new ArrayList<>();

                    categories.add(new Category(0L, "Tất cả"));
                    categories.addAll(response.body());

                    categoryAdapter.setData(categories);
                } else {
                    Toast.makeText(requireContext(), "Không lấy được danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(requireContext(), "Lỗi danh mục: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadProducts() {
        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    showHomeProducts(response.body());
                } else {
                    Toast.makeText(requireContext(), "Không lấy được danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(requireContext(), "Lỗi API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadProductsByCategory(Long categoryId) {
        ApiClient.getApiService().getProductsByCategory(categoryId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    showHomeProducts(response.body());
                } else {
                    Toast.makeText(requireContext(), "Không lấy được sản phẩm theo danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(requireContext(), "Lỗi lọc sản phẩm: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
                if (!isAdded()) {
                    return;
                }

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
                if (txtCartBadge != null) {
                    txtCartBadge.setVisibility(View.GONE);
                }
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

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}