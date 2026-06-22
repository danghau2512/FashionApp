package com.example.fashionshopmobile.model;

import java.math.BigDecimal;

public class AdminDashboard {

    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long pendingOrders;
    private Long cancelledOrders;
    private Long totalProducts;
    private Long totalUsers;
    private Long totalSoldQuantity;

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