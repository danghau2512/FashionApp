package com.example.fashionshop.repository;

import com.example.fashionshop.entity.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {

    List<ShopOrder> findByUser_IdOrderByCreatedAtDesc(Long userId);
}