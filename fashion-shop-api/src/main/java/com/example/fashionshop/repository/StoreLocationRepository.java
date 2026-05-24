package com.example.fashionshop.repository;

import com.example.fashionshop.entity.StoreLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreLocationRepository extends JpaRepository<StoreLocation, Long> {

    List<StoreLocation> findByStatus(String status);

    Optional<StoreLocation> findByIdAndStatus(Long id, String status);
}