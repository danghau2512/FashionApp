package com.example.fashionshop.repository;

import com.example.fashionshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(String status);

    List<Product> findByNameContainingIgnoreCaseAndStatus(String keyword, String status);
}