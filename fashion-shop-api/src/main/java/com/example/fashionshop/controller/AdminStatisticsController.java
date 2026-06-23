package com.example.fashionshop.controller;

import com.example.fashionshop.dto.AdminStatisticsResponse;
import com.example.fashionshop.service.AdminStatisticsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@CrossOrigin("*")
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    public AdminStatisticsController(AdminStatisticsService adminStatisticsService) {
        this.adminStatisticsService = adminStatisticsService;
    }

    @GetMapping
    public AdminStatisticsResponse getAdminStatistics(@RequestParam Long adminId,
                                                      @RequestParam(required = false) Integer year,
                                                      @RequestParam(required = false) Integer bestSellerMonths,
                                                      @RequestParam(required = false) Integer noSaleMonths) {
        return adminStatisticsService.getAdminStatistics(
                adminId,
                year,
                bestSellerMonths,
                noSaleMonths
        );
    }
}