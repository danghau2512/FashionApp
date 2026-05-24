package com.example.fashionshop.service;

import com.example.fashionshop.dto.StoreLocationResponse;
import com.example.fashionshop.entity.StoreLocation;
import com.example.fashionshop.repository.StoreLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreLocationService {

    private final StoreLocationRepository storeLocationRepository;

    public StoreLocationService(StoreLocationRepository storeLocationRepository) {
        this.storeLocationRepository = storeLocationRepository;
    }

    public List<StoreLocationResponse> getAllStores() {
        return storeLocationRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::toStoreLocationResponse)
                .toList();
    }

    public StoreLocationResponse getStoreById(Long id) {
        StoreLocation store = storeLocationRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cửa hàng"));

        return toStoreLocationResponse(store);
    }

    private StoreLocationResponse toStoreLocationResponse(StoreLocation store) {
        return new StoreLocationResponse(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getPhone(),
                store.getLatitude(),
                store.getLongitude()
        );
    }
}