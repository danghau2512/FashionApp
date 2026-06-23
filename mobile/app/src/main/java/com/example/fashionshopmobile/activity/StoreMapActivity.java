package com.example.fashionshopmobile.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.StoreLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    private ImageButton btnBack;
    private MaterialCardView cardStoreInfo;
    private TextView tvStoreName;
    private TextView tvStoreAddress;
    private TextView tvStorePhone;
    private MaterialButton btnDirections;

    private StoreLocation selectedStore;

    private final List<StoreLocation> storeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map);

        initViews();
        setupEvents();
        initMap();
        loadStores();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardStoreInfo = findViewById(R.id.cardStoreInfo);
        tvStoreName = findViewById(R.id.tvStoreName);
        tvStoreAddress = findViewById(R.id.tvStoreAddress);
        tvStorePhone = findViewById(R.id.tvStorePhone);
        btnDirections = findViewById(R.id.btnDirections);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(view -> finish());
        btnDirections.setOnClickListener(view -> openDirections());
    }

    private void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMarkerClickListener(marker -> {
            Object markerData = marker.getTag();

            if (markerData instanceof StoreLocation) {
                StoreLocation store = (StoreLocation) markerData;
                showStoreInfo(store);

                marker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f));

                return true;
            }

            return false;
        });

        googleMap.setOnMapClickListener(latLng -> hideStoreInfo());

        renderStoreMarkers();
    }

    private void loadStores() {
        ApiClient.getApiService().getStores().enqueue(new Callback<List<StoreLocation>>() {

            @Override
            public void onResponse(@NonNull Call<List<StoreLocation>> call, @NonNull Response<List<StoreLocation>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(StoreMapActivity.this, "Không lấy được danh sách cửa hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

                storeList.clear();
                storeList.addAll(response.body());

                if (storeList.isEmpty()) {
                    Toast.makeText(StoreMapActivity.this, "Hiện chưa có cửa hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

                renderStoreMarkers();
            }

            @Override
            public void onFailure(@NonNull Call<List<StoreLocation>> call, @NonNull Throwable throwable) {
                Toast.makeText(StoreMapActivity.this, "Lỗi API: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderStoreMarkers() {
        if (googleMap == null || storeList.isEmpty()) {
            return;
        }

        googleMap.clear();

        List<LatLng> validLocations = new ArrayList<>();

        for (StoreLocation store : storeList) {
            if (store.getLatitude() == null || store.getLongitude() == null) {
                continue;
            }

            if (store.getStatus() != null && !"ACTIVE".equalsIgnoreCase(store.getStatus())) {
                continue;
            }

            LatLng storePosition = new LatLng(store.getLatitude(), store.getLongitude());

            String storeName = store.getName() == null || store.getName().isEmpty() ? "Fashion Shop" : store.getName();
            String storeAddress = store.getAddress() == null || store.getAddress().isEmpty() ? "Chưa cập nhật địa chỉ" : store.getAddress();

            Marker marker = googleMap.addMarker(new MarkerOptions().position(storePosition).title(storeName).snippet(storeAddress));

            if (marker != null) {
                marker.setTag(store);
            }

            validLocations.add(storePosition);
        }

        if (validLocations.isEmpty()) {
            Toast.makeText(this, "Không có cửa hàng có tọa độ hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        moveCameraToStores(validLocations);
    }

    private void showStoreInfo(StoreLocation store) {
        selectedStore = store;

        String storeName = store.getName() == null || store.getName().isEmpty() ? "Fashion Shop" : store.getName();
        String storeAddress = store.getAddress() == null || store.getAddress().isEmpty() ? "Chưa cập nhật địa chỉ" : store.getAddress();
        String storePhone = store.getPhone() == null || store.getPhone().isEmpty() ? "Chưa cập nhật số điện thoại" : store.getPhone();

        tvStoreName.setText(storeName);
        tvStoreAddress.setText(storeAddress);
        tvStorePhone.setText("Điện thoại: " + storePhone);

        cardStoreInfo.setVisibility(View.VISIBLE);

        if (googleMap != null) {
            int bottomPadding = (int) (210 * getResources().getDisplayMetrics().density);
            googleMap.setPadding(0, 0, 0, bottomPadding);
        }
    }

    private void hideStoreInfo() {
        selectedStore = null;
        cardStoreInfo.setVisibility(View.GONE);

        if (googleMap != null) {
            googleMap.setPadding(0, 0, 0, 0);
        }
    }

    private void openDirections() {
        if (selectedStore == null || selectedStore.getLatitude() == null || selectedStore.getLongitude() == null) {
            Toast.makeText(this, "Cửa hàng chưa có tọa độ", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = selectedStore.getLatitude();
        double longitude = selectedStore.getLongitude();

        Uri directionUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1"
                        + "&destination=" + latitude + "," + longitude
                        + "&travelmode=driving"
        );

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, directionUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException exception) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, directionUri);
            startActivity(browserIntent);
        }
    }

    private void openDirectionsInBrowser(double latitude, double longitude) {
        Uri browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
        startActivity(browserIntent);
    }

    private void moveCameraToStores(List<LatLng> locations) {
        if (googleMap == null || locations.isEmpty()) {
            return;
        }

        if (locations.size() == 1) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 15f));
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (LatLng location : locations) {
            boundsBuilder.include(location);
        }

        LatLngBounds bounds = boundsBuilder.build();

        if (mapFragment != null && mapFragment.getView() != null) {
            mapFragment.getView().post(() -> googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120)));
        }
    }
}