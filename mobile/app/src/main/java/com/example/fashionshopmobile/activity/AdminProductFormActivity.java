package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminProductResponse;
import com.example.fashionshopmobile.model.Category;
import com.example.fashionshopmobile.request.AdminProductRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.fashionshopmobile.model.ImageUploadResponse;
import com.example.fashionshopmobile.utils.ImageUrlUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AdminProductFormActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView txtTitle;
    private TextView btnSave;
    private TextView btnCancel;
    private TextView txtMessage;
    private TextView btnChooseImage;
    private ImageView imgPreview;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedImageUri;

    private EditText edtName;
    private EditText edtDescription;
    private EditText edtPrice;
    private EditText edtSalePrice;
    private EditText edtImageUrl;
    private EditText edtBrand;

    private Spinner spinnerCategory;
    private Spinner spinnerGender;
    private Spinner spinnerStatus;

    private SessionManager sessionManager;

    private Long productId;
    private boolean editMode = false;

    private final List<Category> categories = new ArrayList<>();
    private final List<String> categoryNames = new ArrayList<>();

    private final String[] genderLabels = {"Nam", "Nữ", "Unisex"};
    private final String[] genderValues = {"MEN", "WOMEN", "UNISEX"};

    private final String[] statusLabels = {"Đang bán", "Đã ẩn"};
    private final String[] statusValues = {"ACTIVE", "INACTIVE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_form);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền quản lý sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productId = getIntent().getLongExtra("productId", -1);
        editMode = productId != null && productId > 0;

        initViews();
        setupImagePicker();
        setupStaticSpinners();
        setupEvents();

        txtTitle.setText(editMode ? "Sửa sản phẩm" : "Thêm sản phẩm");

        loadCategories();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackAdminProductForm);
        txtTitle = findViewById(R.id.txtAdminProductFormTitle);
        btnSave = findViewById(R.id.btnSaveAdminProduct);
        btnCancel = findViewById(R.id.btnCancelAdminProductForm);
        txtMessage = findViewById(R.id.txtAdminProductFormMessage);
        btnChooseImage = findViewById(R.id.btnChooseAdminProductImage);
        imgPreview = findViewById(R.id.imgAdminProductPreview);

        edtName = findViewById(R.id.edtAdminProductName);
        edtDescription = findViewById(R.id.edtAdminProductDescription);
        edtPrice = findViewById(R.id.edtAdminProductPrice);
        edtSalePrice = findViewById(R.id.edtAdminProductSalePrice);
        edtImageUrl = findViewById(R.id.edtAdminProductImageUrl);
        edtBrand = findViewById(R.id.edtAdminProductBrand);

        spinnerCategory = findViewById(R.id.spinnerAdminProductCategory);
        spinnerGender = findViewById(R.id.spinnerAdminProductGender);
        spinnerStatus = findViewById(R.id.spinnerAdminProductStatusForm);
    }

    private void setupStaticSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genderLabels
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusLabels
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProduct());

        btnChooseImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void loadCategories() {
        txtMessage.setText("Đang tải danh mục...");

        ApiClient.getApiService()
                .getCategories()
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categories.clear();
                            categoryNames.clear();

                            for (Category category : response.body()) {
                                categories.add(category);
                                categoryNames.add(category.getName());
                            }

                            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                                    AdminProductFormActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    categoryNames
                            );
                            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategory.setAdapter(categoryAdapter);

                            txtMessage.setText("");

                            if (editMode) {
                                loadProductDetail();
                            }
                        } else {
                            txtMessage.setText("Không tải được danh mục");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        txtMessage.setText("Lỗi API danh mục: " + t.getMessage());
                    }
                });
    }

    private void loadProductDetail() {
        txtMessage.setText("Đang tải sản phẩm...");

        ApiClient.getApiService()
                .getAdminProductById(productId)
                .enqueue(new Callback<AdminProductResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductResponse> call,
                                           Response<AdminProductResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fillProductToForm(response.body());
                            txtMessage.setText("");
                        } else {
                            txtMessage.setText("Không tải được chi tiết sản phẩm");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductResponse> call, Throwable t) {
                        txtMessage.setText("Lỗi API sản phẩm: " + t.getMessage());
                    }
                });
    }

    private void fillProductToForm(AdminProductResponse product) {
        edtName.setText(product.getName());
        edtDescription.setText(product.getDescription());
        edtPrice.setText(product.getPrice() != null ? product.getPrice().toPlainString() : "");
        edtSalePrice.setText(product.getSalePrice() != null ? product.getSalePrice().toPlainString() : "");
        edtImageUrl.setText(product.getImageUrl());
        Glide.with(this)
                .load(ImageUrlUtils.getFullImageUrl(product.getImageUrl()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgPreview);
        edtBrand.setText(product.getBrand());

        selectCategory(product.getCategoryId());
        selectGender(product.getGender());
        selectStatus(product.getStatus());
    }

    private void selectCategory(Long categoryId) {
        if (categoryId == null) return;

        for (int i = 0; i < categories.size(); i++) {
            if (categoryId.equals(categories.get(i).getId())) {
                spinnerCategory.setSelection(i);
                return;
            }
        }
    }

    private void selectGender(String gender) {
        if (gender == null) return;

        for (int i = 0; i < genderValues.length; i++) {
            if (genderValues[i].equalsIgnoreCase(gender)) {
                spinnerGender.setSelection(i);
                return;
            }
        }
    }

    private void selectStatus(String status) {
        if (status == null) return;

        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i].equalsIgnoreCase(status)) {
                spinnerStatus.setSelection(i);
                return;
            }
        }
    }

    private void saveProduct() {
        txtMessage.setText("");

        if (categories.isEmpty()) {
            txtMessage.setText("Chưa có danh mục để chọn");
            return;
        }

        String name = edtName.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String salePriceText = edtSalePrice.getText().toString().trim();
        String imageUrl = edtImageUrl.getText().toString().trim();
        String brand = edtBrand.getText().toString().trim();

        if (name.isEmpty()) {
            txtMessage.setText("Tên sản phẩm không được để trống");
            return;
        }

        BigDecimal price;

        try {
            price = new BigDecimal(priceText);
        } catch (Exception e) {
            txtMessage.setText("Giá sản phẩm không hợp lệ");
            return;
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            txtMessage.setText("Giá sản phẩm phải lớn hơn 0");
            return;
        }

        BigDecimal salePrice = null;

        if (!salePriceText.isEmpty()) {
            try {
                salePrice = new BigDecimal(salePriceText);
            } catch (Exception e) {
                txtMessage.setText("Giá sale không hợp lệ");
                return;
            }

            if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
                txtMessage.setText("Giá sale không được âm");
                return;
            }

            if (salePrice.compareTo(price) > 0) {
                txtMessage.setText("Giá sale không được lớn hơn giá gốc");
                return;
            }
        }

        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        Long categoryId = categories.get(categoryPosition).getId();

        String gender = genderValues[spinnerGender.getSelectedItemPosition()];
        String status = statusValues[spinnerStatus.getSelectedItemPosition()];

        Long adminId = sessionManager.getUserId();

        if (adminId == null) {
            txtMessage.setText("Không tìm thấy adminId, vui lòng đăng nhập lại");
            return;
        }

        btnSave.setEnabled(false);

        if (selectedImageUri != null) {
            uploadSelectedImageThenSave(
                    adminId,
                    categoryId,
                    name,
                    description,
                    price,
                    salePrice,
                    brand,
                    gender,
                    status
            );
        } else {
            AdminProductRequest request = new AdminProductRequest(
                    categoryId,
                    name,
                    description,
                    price,
                    salePrice,
                    imageUrl,
                    brand,
                    gender,
                    status
            );

            txtMessage.setText("Đang lưu sản phẩm...");

            if (editMode) {
                updateProduct(adminId, request);
            } else {
                createProduct(adminId, request);
            }
        }
    }

    private void createProduct(Long adminId, AdminProductRequest request) {
        ApiClient.getApiService()
                .createAdminProduct(adminId, request)
                .enqueue(new Callback<AdminProductResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductResponse> call,
                                           Response<AdminProductResponse> response) {
                        btnSave.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(AdminProductFormActivity.this,
                                    "Thêm sản phẩm thành công",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            txtMessage.setText("Thêm sản phẩm thất bại. Kiểm tra dữ liệu hoặc quyền admin");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductResponse> call, Throwable t) {
                        btnSave.setEnabled(true);
                        txtMessage.setText("Lỗi API: " + t.getMessage());
                    }
                });
    }

    private void updateProduct(Long adminId, AdminProductRequest request) {
        ApiClient.getApiService()
                .updateAdminProduct(productId, adminId, request)
                .enqueue(new Callback<AdminProductResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductResponse> call,
                                           Response<AdminProductResponse> response) {
                        btnSave.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(AdminProductFormActivity.this,
                                    "Cập nhật sản phẩm thành công",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            txtMessage.setText("Cập nhật thất bại. Kiểm tra dữ liệu hoặc quyền admin");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductResponse> call, Throwable t) {
                        btnSave.setEnabled(true);
                        txtMessage.setText("Lỗi API: " + t.getMessage());
                    }
                });
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;

                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .into(imgPreview);

                        txtMessage.setText("Đã chọn ảnh. Ảnh sẽ được upload khi bấm Lưu.");
                    }
                }
        );
    }
    private void uploadSelectedImageThenSave(Long adminId,
                                             Long categoryId,
                                             String name,
                                             String description,
                                             BigDecimal price,
                                             BigDecimal salePrice,
                                             String brand,
                                             String gender,
                                             String status) {
        if (selectedImageUri == null) {
            txtMessage.setText("Chưa chọn ảnh để upload");
            btnSave.setEnabled(true);
            return;
        }

        try {
            MultipartBody.Part imagePart = createImagePart(selectedImageUri);

            txtMessage.setText("Đang upload ảnh...");

            ApiClient.getApiService()
                    .uploadAdminImage(adminId, imagePart)
                    .enqueue(new Callback<ImageUploadResponse>() {
                        @Override
                        public void onResponse(Call<ImageUploadResponse> call,
                                               Response<ImageUploadResponse> response) {
                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().getImageUrl() != null) {

                                String newImageUrl = response.body().getImageUrl();
                                edtImageUrl.setText(newImageUrl);

                                AdminProductRequest request = new AdminProductRequest(
                                        categoryId,
                                        name,
                                        description,
                                        price,
                                        salePrice,
                                        newImageUrl,
                                        brand,
                                        gender,
                                        status
                                );

                                txtMessage.setText("Upload ảnh thành công. Đang lưu sản phẩm...");

                                if (editMode) {
                                    updateProduct(adminId, request);
                                } else {
                                    createProduct(adminId, request);
                                }
                            } else {
                                btnSave.setEnabled(true);
                                txtMessage.setText("Upload ảnh thất bại");
                            }
                        }

                        @Override
                        public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                            btnSave.setEnabled(true);
                            txtMessage.setText("Lỗi upload ảnh: " + t.getMessage());
                        }
                    });

        } catch (Exception e) {
            btnSave.setEnabled(true);
            txtMessage.setText("Không đọc được ảnh: " + e.getMessage());
        }
    }

    private MultipartBody.Part createImagePart(Uri uri) throws IOException {
        String mimeType = getContentResolver().getType(uri);

        if (mimeType == null) {
            mimeType = "image/jpeg";
        }

        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

        if (extension == null) {
            extension = "jpg";
        }

        File tempFile = new File(
                getCacheDir(),
                "upload_" + System.currentTimeMillis() + "." + extension
        );

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
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