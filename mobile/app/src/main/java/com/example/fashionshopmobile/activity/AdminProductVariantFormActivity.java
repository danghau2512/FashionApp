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
import com.example.fashionshopmobile.model.AdminProductVariantResponse;
import com.example.fashionshopmobile.request.AdminProductVariantRequest;
import com.example.fashionshopmobile.utils.SessionManager;

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

public class AdminProductVariantFormActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView txtTitle;
    private TextView btnSave;
    private TextView btnCancel;
    private TextView txtMessage;
    private TextView btnChooseImage;
    private ImageView imgPreview;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedImageUri;

    private EditText edtSize;
    private EditText edtColor;
    private EditText edtQuantity;
    private EditText edtImageUrl;
    private Spinner spinnerStatus;

    private SessionManager sessionManager;

    private Long variantId;
    private Long productId;
    private boolean editMode = false;

    private final String[] statusLabels = {"Đang bán", "Đã ẩn"};
    private final String[] statusValues = {"ACTIVE", "INACTIVE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_variant_form);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Bạn không có quyền quản lý biến thể", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        variantId = getIntent().getLongExtra("variantId", -1);
        productId = getIntent().getLongExtra("productId", -1);

        editMode = variantId != null && variantId > 0;

        if (!editMode && (productId == null || productId <= 0)) {
            Toast.makeText(this, "Thiếu productId để thêm biến thể", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupImagePicker();
        setupStatusSpinner();
        setupEvents();

        txtTitle.setText(editMode ? "Sửa biến thể" : "Thêm biến thể");

        if (editMode) {
            loadVariantDetail();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackAdminVariantForm);
        txtTitle = findViewById(R.id.txtAdminVariantFormTitle);
        btnSave = findViewById(R.id.btnSaveAdminVariant);
        btnCancel = findViewById(R.id.btnCancelAdminVariantForm);
        txtMessage = findViewById(R.id.txtAdminVariantFormMessage);
        btnChooseImage = findViewById(R.id.btnChooseAdminVariantImage);
        imgPreview = findViewById(R.id.imgAdminVariantPreview);

        edtSize = findViewById(R.id.edtAdminVariantSize);
        edtColor = findViewById(R.id.edtAdminVariantColor);
        edtQuantity = findViewById(R.id.edtAdminVariantQuantity);
        edtImageUrl = findViewById(R.id.edtAdminVariantImageUrl);
        spinnerStatus = findViewById(R.id.spinnerAdminVariantStatus);
    }

    private void setupStatusSpinner() {
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
        btnSave.setOnClickListener(v -> saveVariant());

        btnChooseImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void loadVariantDetail() {
        txtMessage.setText("Đang tải biến thể...");

        ApiClient.getApiService()
                .getAdminProductVariantById(variantId)
                .enqueue(new Callback<AdminProductVariantResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductVariantResponse> call,
                                           Response<AdminProductVariantResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fillVariantToForm(response.body());
                            txtMessage.setText("");
                        } else {
                            txtMessage.setText("Không tải được chi tiết biến thể");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductVariantResponse> call, Throwable t) {
                        txtMessage.setText("Lỗi API biến thể: " + t.getMessage());
                    }
                });
    }

    private void fillVariantToForm(AdminProductVariantResponse variant) {
        edtSize.setText(variant.getSize());
        edtColor.setText(variant.getColor());
        edtQuantity.setText(variant.getQuantity() != null ? String.valueOf(variant.getQuantity()) : "");
        edtImageUrl.setText(variant.getImageUrl());
        Glide.with(this)
                .load(ImageUrlUtils.getFullImageUrl(variant.getImageUrl()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imgPreview);

        selectStatus(variant.getStatus());
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

    private void saveVariant() {
        txtMessage.setText("");

        String size = edtSize.getText().toString().trim();
        String color = edtColor.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String imageUrl = edtImageUrl.getText().toString().trim();
        String status = statusValues[spinnerStatus.getSelectedItemPosition()];

        if (size.isEmpty()) {
            txtMessage.setText("Size không được để trống");
            return;
        }

        if (color.isEmpty()) {
            txtMessage.setText("Màu không được để trống");
            return;
        }

        if (quantityText.isEmpty()) {
            txtMessage.setText("Số lượng không được để trống");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (Exception e) {
            txtMessage.setText("Số lượng không hợp lệ");
            return;
        }

        if (quantity < 0) {
            txtMessage.setText("Số lượng không được âm");
            return;
        }

        Long adminId = sessionManager.getUserId();

        if (adminId == null) {
            txtMessage.setText("Không tìm thấy adminId, vui lòng đăng nhập lại");
            return;
        }

        btnSave.setEnabled(false);

        if (selectedImageUri != null) {
            uploadSelectedImageThenSave(
                    adminId,
                    size,
                    color,
                    quantity,
                    status
            );
        } else {
            AdminProductVariantRequest request = new AdminProductVariantRequest(
                    size,
                    color,
                    quantity,
                    imageUrl,
                    status
            );

            txtMessage.setText(editMode ? "Đang cập nhật biến thể..." : "Đang thêm biến thể...");

            if (editMode) {
                updateVariant(adminId, request);
            } else {
                createVariant(adminId, request);
            }
        }
    }

    private void createVariant(Long adminId, AdminProductVariantRequest request) {
        ApiClient.getApiService()
                .createAdminProductVariant(productId, adminId, request)
                .enqueue(new Callback<AdminProductVariantResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductVariantResponse> call,
                                           Response<AdminProductVariantResponse> response) {
                        btnSave.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(AdminProductVariantFormActivity.this,
                                    "Thêm biến thể thành công",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            txtMessage.setText("Thêm biến thể thất bại. Kiểm tra dữ liệu hoặc quyền admin");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductVariantResponse> call, Throwable t) {
                        btnSave.setEnabled(true);
                        txtMessage.setText("Lỗi API: " + t.getMessage());
                    }
                });
    }

    private void updateVariant(Long adminId, AdminProductVariantRequest request) {
        ApiClient.getApiService()
                .updateAdminProductVariant(variantId, adminId, request)
                .enqueue(new Callback<AdminProductVariantResponse>() {
                    @Override
                    public void onResponse(Call<AdminProductVariantResponse> call,
                                           Response<AdminProductVariantResponse> response) {
                        btnSave.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(AdminProductVariantFormActivity.this,
                                    "Cập nhật biến thể thành công",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            txtMessage.setText("Cập nhật biến thể thất bại. Kiểm tra dữ liệu hoặc quyền admin");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProductVariantResponse> call, Throwable t) {
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
                                             String size,
                                             String color,
                                             int quantity,
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

                                AdminProductVariantRequest request = new AdminProductVariantRequest(
                                        size,
                                        color,
                                        quantity,
                                        newImageUrl,
                                        status
                                );

                                txtMessage.setText("Upload ảnh thành công. Đang lưu biến thể...");

                                if (editMode) {
                                    updateVariant(adminId, request);
                                } else {
                                    createVariant(adminId, request);
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