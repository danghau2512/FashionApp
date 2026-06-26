package com.example.fashionshopmobile.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.activity.AddressActivity;
import com.example.fashionshopmobile.activity.EditProfileActivity;
import com.example.fashionshopmobile.activity.LoginActivity;
import com.example.fashionshopmobile.activity.OrderHistoryActivity;
import com.example.fashionshopmobile.activity.ProductDetailActivity;
import com.example.fashionshopmobile.adapter.ProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.OrderSummary;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvFullName, tvEmail, btnCart;
    private TextView tvPendingCount, tvPackingCount, tvShippingCount, tvReviewCount;
    private TextView tvViewOrders;
    private RecyclerView rvSuggestedProducts;

    private SessionManager sessionManager;
    private ProductAdapter productAdapter;

    private Long userId;

    private static final int SUGGEST_LIMIT = 6;

    private TextView layoutAddress, layoutResetPassword, btnLogout;
    private TextView btnAddAvatar;
    private ActivityResultLauncher<String> avatarPickerLauncher;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        if (userId == null) {
            goToLogin();
            return view;
        }

        initViews(view);
        setupSuggestedProducts();
        setupClickEvents();

        showSessionUser();
        loadUserProfile();
        loadOrderStatus();
        loadSuggestedProducts();

        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAvatarPicker();
    }

    private void initViews(View view) {
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnCart = view.findViewById(R.id.btnCart);

        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvPackingCount = view.findViewById(R.id.tvPackingCount);
        tvShippingCount = view.findViewById(R.id.tvShippingCount);
        tvReviewCount = view.findViewById(R.id.tvReviewCount);

        tvViewOrders = view.findViewById(R.id.tvViewOrders);
        rvSuggestedProducts = view.findViewById(R.id.rvSuggestedProducts);

        layoutAddress = view.findViewById(R.id.layoutAddress);
        layoutResetPassword = view.findViewById(R.id.layoutResetPassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnAddAvatar = view.findViewById(R.id.btnAddAvatar);
    }

    private void showSessionUser() {
        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();

        if (fullName != null && !fullName.isEmpty()) {
            tvFullName.setText(fullName);
        } else {
            tvFullName.setText("Người dùng");
        }

        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
        } else {
            tvEmail.setText("Chưa có email");
        }
    }

    private void loadUserProfile() {
        ApiClient.getApiService().getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    tvFullName.setText(getDisplayName(user));
                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "Chưa có email");

                    sessionManager.saveUser(user);
                    loadAvatar(user.getAvatarUrl());
                } else {
                    Toast.makeText(requireContext(), "Không lấy được thông tin tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(requireContext(), "Lỗi tài khoản: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getDisplayName(User user) {
        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            return user.getFullName();
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            return user.getEmail();
        }

        return "Người dùng";
    }

    private void loadAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            imgAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
            return;
        }

        Glide.with(ProfileFragment.this)
                .load(buildImageUrl(avatarUrl))
                .circleCrop()
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(imgAvatar);
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

    private void loadOrderStatus() {
        ApiClient.getApiService().getOrdersByUserId(userId).enqueue(new Callback<List<OrderSummary>>() {
            @Override
            public void onResponse(Call<List<OrderSummary>> call, Response<List<OrderSummary>> response) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    showOrderCount(response.body());
                } else {
                    Toast.makeText(requireContext(), "Không lấy được đơn mua", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderSummary>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(requireContext(), "Lỗi đơn mua: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOrderCount(List<OrderSummary> orders) {
        int pending = 0;
        int packing = 0;
        int shipping = 0;
        int review = 0;

        for (OrderSummary order : orders) {
            String status = order.getOrderStatus();

            if (status == null) {
                continue;
            }

            if (status.equals("PENDING")) {
                pending++;
            } else if (status.equals("CONFIRMED") || status.equals("PROCESSING") || status.equals("PACKING")) {
                packing++;
            } else if (status.equals("SHIPPING") || status.equals("DELIVERING")) {
                shipping++;
            } else if (status.equals("DELIVERED") || status.equals("COMPLETED")) {
                review++;
            }
        }

        tvPendingCount.setText(String.valueOf(pending));
        tvPackingCount.setText(String.valueOf(packing));
        tvShippingCount.setText(String.valueOf(shipping));
        tvReviewCount.setText(String.valueOf(review));
    }

    private void setupSuggestedProducts() {
        productAdapter = new ProductAdapter(product -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        rvSuggestedProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvSuggestedProducts.setAdapter(productAdapter);
    }

    private void loadSuggestedProducts() {
        ApiClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    if (products.isEmpty()) {
                        productAdapter.setData(new ArrayList<>());
                        return;
                    }

                    int endIndex = Math.min(products.size(), SUGGEST_LIMIT);
                    List<Product> suggestedProducts = new ArrayList<>(products.subList(0, endIndex));

                    productAdapter.setData(suggestedProducts);
                } else {
                    Toast.makeText(requireContext(), "Không lấy được gợi ý sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(requireContext(), "Lỗi gợi ý: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickEvents() {
        btnCart.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Chức năng giỏ hàng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        tvViewOrders.setOnClickListener(v -> openOrderHistory());

        imgAvatar.setOnClickListener(v -> openEditProfile());
        tvFullName.setOnClickListener(v -> openEditProfile());
        btnAddAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));

        layoutAddress.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddressActivity.class);
            startActivity(intent);
        });

        layoutResetPassword.setOnClickListener(v -> sendResetPasswordEmail());

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void openOrderHistory() {
        Intent intent = new Intent(requireContext(), OrderHistoryActivity.class);
        startActivity(intent);
    }
    private void openEditProfile() {
        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void goToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        requireActivity().finish();
    }

    private void sendResetPasswordEmail() {
        String email = sessionManager.getEmail();

        if (email == null || email.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy email tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Đặt lại mật khẩu")
                .setMessage("Hệ thống sẽ gửi email đặt lại mật khẩu đến:\n" + email)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Gửi email", (dialog, which) -> {
                    FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (!isAdded()) {
                                    return;
                                }

                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(requireContext(), "Gửi email thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    sessionManager.logout();

                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    requireActivity().finish();
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sessionManager != null && sessionManager.getUserId() != null) {
            showSessionUser();
            loadUserProfile();
            loadOrderStatus();
            loadSuggestedProducts();
        }
    }
    private void setupAvatarPicker() {
        avatarPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadAvatar(uri);
                    }
                }
        );
    }

    private void uploadAvatar(Uri uri) {
        if (userId == null) {
            Toast.makeText(requireContext(), "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MultipartBody.Part imagePart = createImagePart(uri);

            btnAddAvatar.setEnabled(false);
            Toast.makeText(requireContext(), "Đang upload ảnh đại diện...", Toast.LENGTH_SHORT).show();

            ApiClient.getApiService()
                    .uploadUserAvatar(userId, imagePart)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (!isAdded()) {
                                return;
                            }

                            btnAddAvatar.setEnabled(true);

                            if (response.isSuccessful() && response.body() != null) {
                                User updatedUser = response.body();

                                sessionManager.saveUser(updatedUser);
                                loadAvatar(updatedUser.getAvatarUrl());

                                Toast.makeText(requireContext(), "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMessage = "Upload thất bại. Code: " + response.code();

                                try {
                                    if (response.errorBody() != null) {
                                        errorMessage += "\n" + response.errorBody().string();
                                    }
                                } catch (Exception e) {
                                    errorMessage += "\nKhông đọc được lỗi server";
                                }

                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            if (!isAdded()) {
                                return;
                            }

                            btnAddAvatar.setEnabled(true);
                            Toast.makeText(requireContext(), "Lỗi upload ảnh: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            btnAddAvatar.setEnabled(true);
            Toast.makeText(requireContext(), "Không đọc được ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private MultipartBody.Part createImagePart(Uri uri) throws IOException {
        String mimeType = requireContext().getContentResolver().getType(uri);

        if (mimeType == null) {
            mimeType = "image/jpeg";
        }

        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

        if (extension == null) {
            extension = "jpg";
        }

        File tempFile = new File(
                requireContext().getCacheDir(),
                "avatar_" + System.currentTimeMillis() + "." + extension
        );

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            if (inputStream == null) {
                throw new IOException("Không mở được file ảnh");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), tempFile);
        return MultipartBody.Part.createFormData("file", tempFile.getName(), requestBody);
    }
}