package com.example.fashionshop.repository;

import com.example.fashionshop.entity.UserProductEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProductEventRepository extends JpaRepository<UserProductEvent, Long> {

    List<UserProductEvent> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<UserProductEvent> findByProduct_IdOrderByCreatedAtDesc(Long productId);
}