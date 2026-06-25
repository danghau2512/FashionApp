package com.example.fashionshopmobile.model;

import java.math.BigDecimal;
import java.util.List;

public class AdminStatistics {

    private Integer year;
    private BigDecimal totalRevenue;
    private List<MonthlyRevenue> monthlyRevenue;
    private Integer bestSellerMonths;
    private List<ProductStatistic> bestSellers;
    private Integer noSaleMonths;
    private List<ProductStatistic> noSaleProducts;

    public Integer getYear() {
        return year;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public List<MonthlyRevenue> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public Integer getBestSellerMonths() {
        return bestSellerMonths;
    }

    public List<ProductStatistic> getBestSellers() {
        return bestSellers;
    }

    public Integer getNoSaleMonths() {
        return noSaleMonths;
    }

    public List<ProductStatistic> getNoSaleProducts() {
        return noSaleProducts;
    }
}