package com.example.fashionshop.controller;

import com.example.fashionshop.dto.AdminOrderActionRequest;
import com.example.fashionshop.dto.AdminOrderDetailResponse;
import com.example.fashionshop.dto.AdminOrderSummaryResponse;
import com.example.fashionshop.service.AdminOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin("*")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public List<AdminOrderSummaryResponse> getAdminOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return adminOrderService.getAdminOrders(keyword, status);
    }

    @GetMapping("/{orderId}")
    public AdminOrderDetailResponse getAdminOrderDetail(@PathVariable Long orderId) {
        return adminOrderService.getAdminOrderDetail(orderId);
    }

    @PutMapping("/{orderId}/confirm")
    public AdminOrderDetailResponse confirmOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody AdminOrderActionRequest request
    ) {
        return adminOrderService.confirmOrderByAdmin(orderId, request.getAdminId());
    }

    @PutMapping("/{orderId}/cancel-by-admin")
    public AdminOrderDetailResponse cancelOrderByAdmin(
            @PathVariable Long orderId,
            @Valid @RequestBody AdminOrderActionRequest request
    ) {
        return adminOrderService.cancelOrderByAdmin(orderId, request.getAdminId(), request.getCancelReason());
    }
}
