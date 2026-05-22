package com.example.fashionshop.controller;

import com.example.fashionshop.dto.StoreLocationResponse;
import com.example.fashionshop.service.StoreLocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin("*")
public class StoreLocationController {

    private final StoreLocationService storeLocationService;

    public StoreLocationController(StoreLocationService storeLocationService) {
        this.storeLocationService = storeLocationService;
    }

    @GetMapping
    public List<StoreLocationResponse> getAllStores() {
        return storeLocationService.getAllStores();
    }

    @GetMapping("/{id}")
    public StoreLocationResponse getStoreById(@PathVariable Long id) {
        return storeLocationService.getStoreById(id);
    }
}