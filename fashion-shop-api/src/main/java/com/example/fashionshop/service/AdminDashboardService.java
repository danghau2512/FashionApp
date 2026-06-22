package com.example.fashionshop.service;

import com.example.fashionshop.dto.AdminDashboardResponse;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.ShopOrderRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminDashboardService {

    private final ShopOrderRepository shopOrderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminDashboardService(ShopOrderRepository shopOrderRepository,
                                 ProductRepository productRepository,
                                 UserRepository userRepository) {
        this.shopOrderRepository = shopOrderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public AdminDashboardResponse getDashboard() {
        BigDecimal totalRevenue = shopOrderRepository.getTotalRevenue();

        Long totalOrders = shopOrderRepository.count();
        Long pendingOrders = shopOrderRepository.countByOrderStatus("PENDING");
        Long cancelledOrders = shopOrderRepository.countByOrderStatus("CANCELLED");

        Long totalProducts = productRepository.countByStatus("ACTIVE");
        Long totalUsers = userRepository.count();

        Long totalSoldQuantity = productRepository.getTotalSoldQuantity();

        return new AdminDashboardResponse(
                totalRevenue,
                totalOrders,
                pendingOrders,
                cancelledOrders,
                totalProducts,
                totalUsers,
                totalSoldQuantity
        );
    }
}