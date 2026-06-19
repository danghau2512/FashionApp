package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.UserAddress;
import com.example.fashionshopmobile.request.AddressRequest;
import com.example.fashionshopmobile.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditAddressActivity extends AppCompatActivity {

    private TextView btnBack, tvTitle;
    private EditText edtReceiverName, edtReceiverPhone, edtProvince, edtDistrict, edtWard, edtAddressDetail;
    private CheckBox cbDefaultAddress;
    private Button btnSaveAddress;

    private SessionManager sessionManager;
    private Long userId;
    private Long addressId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            finish();
            return;
        }

        initViews();
        readIntentData();
        setupClickEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtReceiverPhone = findViewById(R.id.edtReceiverPhone);
        edtProvince = findViewById(R.id.edtProvince);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtWard = findViewById(R.id.edtWard);
        edtAddressDetail = findViewById(R.id.edtAddressDetail);

        cbDefaultAddress = findViewById(R.id.cbDefaultAddress);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
    }

    private void readIntentData() {
        if (getIntent().hasExtra("address_id")) {
            isEditMode = true;
            addressId = getIntent().getLongExtra("address_id", -1);

            tvTitle.setText("Sửa địa chỉ");

            edtReceiverName.setText(getIntent().getStringExtra("receiver_name"));
            edtReceiverPhone.setText(getIntent().getStringExtra("receiver_phone"));
            edtAddressDetail.setText(getIntent().getStringExtra("address_detail"));
            edtWard.setText(getIntent().getStringExtra("ward"));
            edtDistrict.setText(getIntent().getStringExtra("district"));
            edtProvince.setText(getIntent().getStringExtra("province"));
            cbDefaultAddress.setChecked(getIntent().getBooleanExtra("default_address", false));
        } else {
            isEditMode = false;
            tvTitle.setText("Thêm địa chỉ");
        }
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnSaveAddress.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String receiverName = edtReceiverName.getText().toString().trim();
        String receiverPhone = edtReceiverPhone.getText().toString().trim();
        String province = edtProvince.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();
        String addressDetail = edtAddressDetail.getText().toString().trim();
        boolean defaultAddress = cbDefaultAddress.isChecked();

        if (receiverName.isEmpty()) {
            edtReceiverName.setError("Tên người nhận không được để trống");
            edtReceiverName.requestFocus();
            return;
        }

        if (receiverPhone.isEmpty()) {
            edtReceiverPhone.setError("Số điện thoại không được để trống");
            edtReceiverPhone.requestFocus();
            return;
        }

        if (!receiverPhone.matches("^[0-9]{9,11}$")) {
            edtReceiverPhone.setError("Số điện thoại không hợp lệ");
            edtReceiverPhone.requestFocus();
            return;
        }

        if (addressDetail.isEmpty()) {
            edtAddressDetail.setError("Địa chỉ chi tiết không được để trống");
            edtAddressDetail.requestFocus();
            return;
        }

        AddressRequest request = new AddressRequest(
                userId,
                receiverName,
                receiverPhone,
                addressDetail,
                ward,
                district,
                province,
                defaultAddress
        );

        btnSaveAddress.setEnabled(false);

        if (isEditMode) {
            updateAddress(request);
        } else {
            createAddress(request);
        }
    }

    private void createAddress(AddressRequest request) {
        ApiClient.getApiService().createAddress(request).enqueue(new Callback<UserAddress>() {
            @Override
            public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                btnSaveAddress.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(AddEditAddressActivity.this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEditAddressActivity.this, "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserAddress> call, Throwable t) {
                btnSaveAddress.setEnabled(true);
                Toast.makeText(AddEditAddressActivity.this, "Lỗi thêm địa chỉ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateAddress(AddressRequest request) {
        ApiClient.getApiService().updateAddress(addressId, request).enqueue(new Callback<UserAddress>() {
            @Override
            public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                btnSaveAddress.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(AddEditAddressActivity.this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEditAddressActivity.this, "Cập nhật địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserAddress> call, Throwable t) {
                btnSaveAddress.setEnabled(true);
                Toast.makeText(AddEditAddressActivity.this, "Lỗi cập nhật địa chỉ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}