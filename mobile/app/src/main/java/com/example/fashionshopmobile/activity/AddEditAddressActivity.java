package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.fashionshopmobile.model.shipping.GhnDistrict;
import com.example.fashionshopmobile.model.shipping.GhnProvince;
import com.example.fashionshopmobile.model.shipping.GhnWard;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
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
    private EditText edtReceiverName, edtReceiverPhone, edtAddressDetail;
    private CheckBox cbDefaultAddress;
    private Button btnSaveAddress;

    private SessionManager sessionManager;
    private Long userId;
    private Long addressId;
    private boolean isEditMode = false;
    private MaterialAutoCompleteTextView actProvince;
    private MaterialAutoCompleteTextView actDistrict;
    private MaterialAutoCompleteTextView actWard;

    private GhnProvince selectedProvince;
    private GhnDistrict selectedDistrict;
    private GhnWard selectedWard;
    private Integer editProvinceId;
    private Integer editDistrictId;
    private String editWardCode;

    private String editProvinceName;
    private String editDistrictName;
    private String editWardName;
    private Double confirmedLatitude;
    private Double confirmedLongitude;

    private boolean restoringEditAddress = false;
    private final List<GhnProvince> provinceList = new ArrayList<>();
    private final List<GhnDistrict> districtList = new ArrayList<>();
    private final List<GhnWard> wardList = new ArrayList<>();

    private ArrayAdapter<GhnProvince> provinceAdapter;
    private ArrayAdapter<GhnDistrict> districtAdapter;
    private ArrayAdapter<GhnWard> wardAdapter;
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
        setupClickEvents();
        setupAddressDropdowns();
        readIntentData();
        loadProvinces();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtReceiverPhone = findViewById(R.id.edtReceiverPhone);
        edtAddressDetail = findViewById(R.id.edtAddressDetail);
        actProvince = findViewById(R.id.actProvince);
        actDistrict = findViewById(R.id.actDistrict);
        actWard = findViewById(R.id.actWard);
        cbDefaultAddress = findViewById(R.id.cbDefaultAddress);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
    }

    private void readIntentData() {
        if (!getIntent().hasExtra("address_id")) {
            isEditMode = false;
            tvTitle.setText("Thêm địa chỉ");
            return;
        }

        isEditMode = true;
        addressId = getIntent().getLongExtra("address_id", -1);

        tvTitle.setText("Sửa địa chỉ");

        edtReceiverName.setText(getIntent().getStringExtra("receiver_name"));
        edtReceiverPhone.setText(getIntent().getStringExtra("receiver_phone"));
        edtAddressDetail.setText(getIntent().getStringExtra("address_detail"));
        cbDefaultAddress.setChecked(getIntent().getBooleanExtra("default_address", false));

        int provinceId = getIntent().getIntExtra("province_id", -1);
        int districtId = getIntent().getIntExtra("district_id", -1);

        editProvinceId = provinceId == -1 ? null : provinceId;
        editDistrictId = districtId == -1 ? null : districtId;
        editWardCode = getIntent().getStringExtra("ward_code");

        editProvinceName = getIntent().getStringExtra("province");
        editDistrictName = getIntent().getStringExtra("district");
        editWardName = getIntent().getStringExtra("ward");

        actProvince.setText(editProvinceName == null ? "" : editProvinceName, false);
        actDistrict.setText(editDistrictName == null ? "" : editDistrictName, false);
        actWard.setText(editWardName == null ? "" : editWardName, false);

        restoringEditAddress = true;
        if (getIntent().hasExtra("latitude")) {
            confirmedLatitude = getIntent().getDoubleExtra("latitude", 0);
        }

        if (getIntent().hasExtra("longitude")) {
            confirmedLongitude = getIntent().getDoubleExtra("longitude", 0);
        }
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnSaveAddress.setOnClickListener(v -> openMapConfirmation());
    }

    private void openMapConfirmation() {
        if (!validateAddressForm()) {
            return;
        }

        Intent intent = new Intent(
                AddEditAddressActivity.this,
                AddressMapConfirmActivity.class
        );

        intent.putExtra("full_address", buildFullAddress());

        // Khi sửa địa chỉ đã có tọa độ thì mở đúng marker cũ.
        if (confirmedLatitude != null && confirmedLongitude != null) {
            intent.putExtra("latitude", confirmedLatitude);
            intent.putExtra("longitude", confirmedLongitude);
        }

        mapConfirmLauncher.launch(intent);
    }

    private String buildFullAddress() {
        String addressDetail = edtAddressDetail.getText().toString().trim();

        return addressDetail
                + ", " + selectedWard.getWardName()
                + ", " + selectedDistrict.getDistrictName()
                + ", " + selectedProvince.getProvinceName();
    }
    private boolean validateAddressForm() {
        String receiverName = edtReceiverName.getText().toString().trim();
        String receiverPhone = edtReceiverPhone.getText().toString().trim();
        String addressDetail = edtAddressDetail.getText().toString().trim();

        if (receiverName.isEmpty()) {
            edtReceiverName.setError("Tên người nhận không được để trống");
            edtReceiverName.requestFocus();
            return false;
        }

        if (receiverPhone.isEmpty()) {
            edtReceiverPhone.setError("Số điện thoại không được để trống");
            edtReceiverPhone.requestFocus();
            return false;
        }

        if (!receiverPhone.matches("^[0-9]{9,11}$")) {
            edtReceiverPhone.setError("Số điện thoại không hợp lệ");
            edtReceiverPhone.requestFocus();
            return false;
        }

        if (selectedProvince == null) {
            actProvince.setError("Vui lòng chọn tỉnh/thành phố");
            actProvince.requestFocus();
            return false;
        }

        if (selectedDistrict == null) {
            actDistrict.setError("Vui lòng chọn quận/huyện");
            actDistrict.requestFocus();
            return false;
        }

        if (selectedWard == null) {
            actWard.setError("Vui lòng chọn phường/xã");
            actWard.requestFocus();
            return false;
        }

        if (addressDetail.isEmpty()) {
            edtAddressDetail.setError("Địa chỉ chi tiết không được để trống");
            edtAddressDetail.requestFocus();
            return false;
        }

        return true;
    }

    private void saveAddressToServer() {
        if (confirmedLatitude == null || confirmedLongitude == null) {
            Toast.makeText(this, "Vui lòng xác nhận vị trí trên bản đồ", Toast.LENGTH_SHORT).show();
            return;
        }

        String receiverName = edtReceiverName.getText().toString().trim();
        String receiverPhone = edtReceiverPhone.getText().toString().trim();
        String addressDetail = edtAddressDetail.getText().toString().trim();

        String province = selectedProvince.getProvinceName();
        String district = selectedDistrict.getDistrictName();
        String ward = selectedWard.getWardName();

        Integer provinceId = selectedProvince.getProvinceId();
        Integer districtId = selectedDistrict.getDistrictId();
        String wardCode = selectedWard.getWardCode();

        boolean defaultAddress = cbDefaultAddress.isChecked();

        AddressRequest request = new AddressRequest(
                userId,
                receiverName,
                receiverPhone,
                addressDetail,
                ward,
                district,
                province,
                provinceId,
                districtId,
                wardCode,
                confirmedLatitude,
                confirmedLongitude,
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
    private void setupAddressDropdowns() {
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinceList);
        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, districtList);
        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, wardList);

        actProvince.setAdapter(provinceAdapter);
        actDistrict.setAdapter(districtAdapter);
        actWard.setAdapter(wardAdapter);

        actProvince.setOnClickListener(view -> actProvince.showDropDown());
        actDistrict.setOnClickListener(view -> actDistrict.showDropDown());
        actWard.setOnClickListener(view -> actWard.showDropDown());

        actProvince.setOnItemClickListener((parent, view, position, id) -> {
            restoringEditAddress = false;

            selectedProvince = (GhnProvince) parent.getItemAtPosition(position);
            selectedDistrict = null;
            selectedWard = null;

            editDistrictId = null;
            editWardCode = null;
            editDistrictName = null;
            editWardName = null;

            actProvince.setError(null);
            actDistrict.setError(null);
            actWard.setError(null);

            actDistrict.setText("", false);
            actWard.setText("", false);

            districtList.clear();
            wardList.clear();

            districtAdapter.notifyDataSetChanged();
            wardAdapter.notifyDataSetChanged();

            actDistrict.setEnabled(true);
            actWard.setEnabled(false);

            loadDistricts(selectedProvince.getProvinceId());
        });

        actDistrict.setOnItemClickListener((parent, view, position, id) -> {
            restoringEditAddress = false;

            selectedDistrict = (GhnDistrict) parent.getItemAtPosition(position);
            selectedWard = null;

            editWardCode = null;
            editWardName = null;

            actDistrict.setError(null);
            actWard.setError(null);

            actWard.setText("", false);
            wardList.clear();
            wardAdapter.notifyDataSetChanged();

            actWard.setEnabled(true);

            loadWards(selectedDistrict.getDistrictId());
        });
        actWard.setOnItemClickListener((parent, view, position, id) -> {
            selectedWard = (GhnWard) parent.getItemAtPosition(position);
            actWard.setError(null);
        });
    }
    private GhnProvince findProvince(Integer provinceId, String provinceName) {
        for (GhnProvince province : provinceList) {
            if (provinceId != null && provinceId.equals(province.getProvinceId())) {
                return province;
            }

            if (provinceId == null && provinceName != null && provinceName.equalsIgnoreCase(province.getProvinceName())) {
                return province;
            }
        }

        return null;
    }

    private GhnDistrict findDistrict(Integer districtId, String districtName) {
        for (GhnDistrict district : districtList) {
            if (districtId != null && districtId.equals(district.getDistrictId())) {
                return district;
            }

            if (districtId == null && districtName != null && districtName.equalsIgnoreCase(district.getDistrictName())) {
                return district;
            }
        }

        return null;
    }

    private GhnWard findWard(String wardCode, String wardName) {
        for (GhnWard ward : wardList) {
            if (wardCode != null && wardCode.equals(ward.getWardCode())) {
                return ward;
            }

            if (wardCode == null && wardName != null && wardName.equalsIgnoreCase(ward.getWardName())) {
                return ward;
            }
        }

        return null;
    }
    private void loadProvinces() {
        actProvince.setEnabled(false);

        ApiClient.getApiService().getGhnProvinces().enqueue(new Callback<List<GhnProvince>>() {
            @Override
            public void onResponse(Call<List<GhnProvince>> call, Response<List<GhnProvince>> response) {
                actProvince.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AddEditAddressActivity.this, "Không lấy được danh sách tỉnh/thành", Toast.LENGTH_SHORT).show();
                    return;
                }

                provinceList.clear();
                provinceList.addAll(response.body());
                provinceAdapter.notifyDataSetChanged();
                if (isEditMode && restoringEditAddress) {
                    selectedProvince = findProvince(editProvinceId, editProvinceName);

                    if (selectedProvince == null) {
                        restoringEditAddress = false;
                        Toast.makeText(AddEditAddressActivity.this, "Không tìm thấy tỉnh/thành phố cũ trong dữ liệu GHN", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    actProvince.setText(selectedProvince.getProvinceName(), false);
                    actDistrict.setEnabled(true);

                    loadDistricts(selectedProvince.getProvinceId());
                }
            }

            @Override
            public void onFailure(Call<List<GhnProvince>> call, Throwable t) {
                actProvince.setEnabled(true);
                Toast.makeText(AddEditAddressActivity.this, "Lỗi tải tỉnh/thành: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void loadDistricts(Integer provinceId) {
        if (provinceId == null) {
            return;
        }

        actDistrict.setEnabled(false);

        ApiClient.getApiService().getGhnDistricts(provinceId).enqueue(new Callback<List<GhnDistrict>>() {
            @Override
            public void onResponse(Call<List<GhnDistrict>> call, Response<List<GhnDistrict>> response) {
                actDistrict.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(
                            AddEditAddressActivity.this,
                            "Không lấy được danh sách quận/huyện",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                districtList.clear();
                districtList.addAll(response.body());
                districtAdapter.notifyDataSetChanged();

                if (isEditMode && restoringEditAddress) {
                    selectedDistrict = findDistrict(editDistrictId, editDistrictName);

                    if (selectedDistrict == null) {
                        restoringEditAddress = false;

                        Toast.makeText(
                                AddEditAddressActivity.this,
                                "Không tìm thấy quận/huyện cũ trong dữ liệu GHN",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    actDistrict.setText(selectedDistrict.getDistrictName(), false);
                    actWard.setEnabled(true);

                    loadWards(selectedDistrict.getDistrictId());
                } else {
                    actDistrict.showDropDown();
                }
            }

            @Override
            public void onFailure(Call<List<GhnDistrict>> call, Throwable t) {
                actDistrict.setEnabled(true);

                Toast.makeText(
                        AddEditAddressActivity.this,
                        "Lỗi tải quận/huyện: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
    private void loadWards(Integer districtId) {
        if (districtId == null) {
            return;
        }

        actWard.setEnabled(false);

        ApiClient.getApiService().getGhnWards(districtId).enqueue(new Callback<List<GhnWard>>() {
            @Override
            public void onResponse(Call<List<GhnWard>> call, Response<List<GhnWard>> response) {
                actWard.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(
                            AddEditAddressActivity.this,
                            "Không lấy được danh sách phường/xã",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                wardList.clear();
                wardList.addAll(response.body());
                wardAdapter.notifyDataSetChanged();

                if (isEditMode && restoringEditAddress) {
                    selectedWard = findWard(editWardCode, editWardName);
                    restoringEditAddress = false;

                    if (selectedWard == null) {
                        Toast.makeText(
                                AddEditAddressActivity.this,
                                "Không tìm thấy phường/xã cũ trong dữ liệu GHN",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    actWard.setText(selectedWard.getWardName(), false);
                } else {
                    actWard.showDropDown();
                }
            }





            @Override
            public void onFailure(Call<List<GhnWard>> call, Throwable t) {
                actWard.setEnabled(true);

                Toast.makeText(
                        AddEditAddressActivity.this,
                        "Lỗi tải phường/xã: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> mapConfirmLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                            return;
                        }

                        confirmedLatitude = result.getData().getDoubleExtra("latitude", 0);
                        confirmedLongitude = result.getData().getDoubleExtra("longitude", 0);

                        if (confirmedLatitude == 0 || confirmedLongitude == 0) {
                            Toast.makeText(this, "Tọa độ không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        saveAddressToServer();
                    }
            );

}