package com.example.fashionshop.repository;

import com.example.fashionshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByStatus(String status);

    Optional<Category> findByIdAndStatus(Long id, String status);
}