package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.AddressAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.UserAddress;
import com.example.fashionshopmobile.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {

    private TextView btnBack, tvEmpty;
    private Button btnAddAddress;
    private RecyclerView rvAddresses;

    private SessionManager sessionManager;
    private AddressAdapter addressAdapter;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        rvAddresses = findViewById(R.id.rvAddresses);
    }

    private void setupRecyclerView() {
        addressAdapter = new AddressAdapter(new AddressAdapter.OnAddressClickListener() {
            @Override
            public void onEdit(UserAddress address) {
                openEditAddress(address);
            }

            @Override
            public void onDelete(UserAddress address) {
                confirmDeleteAddress(address);
            }

            @Override
            public void onSetDefault(UserAddress address) {
                setDefaultAddress(address);
            }
        });

        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(addressAdapter);
    }

    private void setupClickEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, AddEditAddressActivity.class);
            startActivity(intent);
        });
    }

    private void loadAddresses() {
        ApiClient.getApiService().getAddressesByUserId(userId).enqueue(new Callback<List<UserAddress>>() {
            @Override
            public void onResponse(Call<List<UserAddress>> call, Response<List<UserAddress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserAddress> addresses = response.body();
                    addressAdapter.setData(addresses);

                    if (addresses.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                    }
                } else {
                    addressAdapter.setData(new ArrayList<>());
                    tvEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(AddressActivity.this, "Không lấy được danh sách địa chỉ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserAddress>> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi địa chỉ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openEditAddress(UserAddress address) {
        Intent intent = new Intent(AddressActivity.this, AddEditAddressActivity.class);

        intent.putExtra("address_id", address.getId());
        intent.putExtra("receiver_name", address.getReceiverName());
        intent.putExtra("receiver_phone", address.getReceiverPhone());
        intent.putExtra("address_detail", address.getAddressDetail());
        intent.putExtra("ward", address.getWard());
        intent.putExtra("district", address.getDistrict());
        intent.putExtra("province", address.getProvince());
        intent.putExtra("default_address", Boolean.TRUE.equals(address.getDefaultAddress()));

        startActivity(intent);
    }

    private void confirmDeleteAddress(UserAddress address) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> deleteAddress(address))
                .show();
    }

    private void deleteAddress(UserAddress address) {
        ApiClient.getApiService().deleteAddress(address.getId(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddressActivity.this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                } else {
                    Toast.makeText(AddressActivity.this, "Xóa địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi xóa: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDefaultAddress(UserAddress address) {
        ApiClient.getApiService().setDefaultAddress(address.getId(), userId).enqueue(new Callback<UserAddress>() {
            @Override
            public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddressActivity.this, "Đã đặt làm địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                } else {
                    Toast.makeText(AddressActivity.this, "Đặt mặc định thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserAddress> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi mặc định: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}