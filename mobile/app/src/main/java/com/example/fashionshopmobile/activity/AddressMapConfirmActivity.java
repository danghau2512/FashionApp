package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressMapConfirmActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker addressMarker;

    private ImageButton btnBack;
    private TextView tvFullAddress;
    private TextView tvCoordinates;
    private MaterialButton btnConfirmLocation;
    private ProgressBar progressBar;

    private String fullAddress;
    private LatLng selectedLocation;

    private Double initialLatitude;
    private Double initialLongitude;

    private final ExecutorService geocoderExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_map_confirm);

        initViews();
        readIntentData();
        setupEvents();
        initMap();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvFullAddress = findViewById(R.id.tvFullAddress);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        btnConfirmLocation = findViewById(R.id.btnConfirmLocation);
        progressBar = findViewById(R.id.progressBar);
    }

    private void readIntentData() {
        fullAddress = getIntent().getStringExtra("full_address");

        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            Toast.makeText(this, "Địa chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvFullAddress.setText(fullAddress);

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            initialLatitude = getIntent().getDoubleExtra("latitude", 0);
            initialLongitude = getIntent().getDoubleExtra("longitude", 0);
        }
    }

    private void setupEvents() {
        btnBack.setOnClickListener(view -> finish());

        btnConfirmLocation.setOnClickListener(view -> confirmLocation());
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

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

        googleMap.setOnMapClickListener(this::setMarkerPosition);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
            }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                updateSelectedLocation(marker.getPosition());
            }
        });

        if (initialLatitude != null && initialLongitude != null && initialLatitude != 0 && initialLongitude != 0) {
            setMarkerPosition(new LatLng(initialLatitude, initialLongitude));
        } else {
            geocodeFullAddress();
        }
    }

    private void geocodeFullAddress() {
        if (!Geocoder.isPresent()) {
            showGeocodeFailure("Thiết bị không hỗ trợ tìm tọa độ từ địa chỉ");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Geocoder geocoder = new Geocoder(this, new Locale("vi", "VN"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(fullAddress, 1, new Geocoder.GeocodeListener() {
                @Override
                public void onGeocode(@NonNull List<Address> addresses) {
                    runOnUiThread(() -> handleGeocodeResult(addresses));
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> showGeocodeFailure("Không tìm thấy vị trí chính xác"));
                }
            });
        } else {
            geocoderExecutor.execute(() -> {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(fullAddress, 1);
                    runOnUiThread(() -> handleGeocodeResult(addresses));
                } catch (IOException exception) {
                    runOnUiThread(() -> showGeocodeFailure("Không thể tìm vị trí từ địa chỉ"));
                }
            });
        }
    }

    private void handleGeocodeResult(List<Address> addresses) {
        progressBar.setVisibility(View.GONE);

        if (addresses == null || addresses.isEmpty()) {
            showGeocodeFailure("Không tìm thấy địa chỉ. Hãy chọn thủ công trên bản đồ");
            return;
        }

        Address address = addresses.get(0);

        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

        setMarkerPosition(location);
    }

    private void showGeocodeFailure(String message) {
        progressBar.setVisibility(View.GONE);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Tạm đưa bản đồ về khu vực Việt Nam để người dùng tự chọn.
        LatLng vietnamCenter = new LatLng(16.047079, 108.206230);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vietnamCenter, 5f));
    }

    private void setMarkerPosition(LatLng location) {
        if (googleMap == null || location == null) {
            return;
        }

        if (addressMarker == null) {
            addressMarker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(location)
                            .title("Vị trí giao hàng")
                            .draggable(true)
            );
        } else {
            addressMarker.setPosition(location);
        }

        updateSelectedLocation(location);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f));
    }

    private void updateSelectedLocation(LatLng location) {
        selectedLocation = location;

        tvCoordinates.setText(
                "Vĩ độ: " + location.latitude
                        + "\nKinh độ: " + location.longitude
        );

        btnConfirmLocation.setEnabled(true);
    }

    private void confirmLocation() {
        if (selectedLocation == null) {
            Toast.makeText(this, "Vui lòng chọn vị trí giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();

        resultIntent.putExtra("latitude", selectedLocation.latitude);
        resultIntent.putExtra("longitude", selectedLocation.longitude);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        geocoderExecutor.shutdownNow();
    }
}