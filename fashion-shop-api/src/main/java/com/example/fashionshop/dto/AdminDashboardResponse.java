package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class AdminDashboardResponse {

    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long pendingOrders;
    private Long cancelledOrders;
    private Long totalProducts;
    private Long totalUsers;
    private Long totalSoldQuantity;

    public AdminDashboardResponse(BigDecimal totalRevenue,
                                  Long totalOrders,
                                  Long pendingOrders,
                                  Long cancelledOrders,
                                  Long totalProducts,
                                  Long totalUsers,
                                  Long totalSoldQuantity) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.cancelledOrders = cancelledOrders;
        this.totalProducts = totalProducts;
        this.totalUsers = totalUsers;
        this.totalSoldQuantity = totalSoldQuantity;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public Long getPendingOrders() {
        return pendingOrders;
    }

    public Long getCancelledOrders() {
        return cancelledOrders;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public Long getTotalSoldQuantity() {
        return totalSoldQuantity;
    }
}