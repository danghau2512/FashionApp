package com.example.fashionshop.dto;

import java.math.BigDecimal;

public class MonthlyRevenueResponse {

    private int month;
    private BigDecimal revenue;

    public MonthlyRevenueResponse(int month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public int getMonth() {
        return month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}