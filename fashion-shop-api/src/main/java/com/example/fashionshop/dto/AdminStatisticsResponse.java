package com.example.fashionshop.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminStatisticsResponse {

    private int year;
    private BigDecimal totalRevenue;
    private List<MonthlyRevenueResponse> monthlyRevenue;
    private int bestSellerMonths;
    private List<ProductStatisticResponse> bestSellers;
    private int noSaleMonths;
    private List<ProductStatisticResponse> noSaleProducts;

    public AdminStatisticsResponse(int year,
                                   BigDecimal totalRevenue,
                                   List<MonthlyRevenueResponse> monthlyRevenue,
                                   int bestSellerMonths,
                                   List<ProductStatisticResponse> bestSellers,
                                   int noSaleMonths,
                                   List<ProductStatisticResponse> noSaleProducts) {
        this.year = year;
        this.totalRevenue = totalRevenue;
        this.monthlyRevenue = monthlyRevenue;
        this.bestSellerMonths = bestSellerMonths;
        this.bestSellers = bestSellers;
        this.noSaleMonths = noSaleMonths;
        this.noSaleProducts = noSaleProducts;
    }

    public int getYear() {
        return year;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public List<MonthlyRevenueResponse> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public int getBestSellerMonths() {
        return bestSellerMonths;
    }

    public List<ProductStatisticResponse> getBestSellers() {
        return bestSellers;
    }

    public int getNoSaleMonths() {
        return noSaleMonths;
    }

    public List<ProductStatisticResponse> getNoSaleProducts() {
        return noSaleProducts;
    }
}