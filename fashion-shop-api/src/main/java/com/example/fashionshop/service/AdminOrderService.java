package com.example.fashionshop.service;

import com.example.fashionshop.dto.AdminOrderDetailResponse;
import com.example.fashionshop.dto.AdminOrderItemResponse;
import com.example.fashionshop.dto.AdminOrderSummaryResponse;
import com.example.fashionshop.entity.OrderItem;
import com.example.fashionshop.entity.ProductVariant;
import com.example.fashionshop.entity.ShopOrder;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.AdminOrderProjection;
import com.example.fashionshop.repository.AdminOrderRepository;
import com.example.fashionshop.repository.OrderItemRepository;
import com.example.fashionshop.repository.ProductVariantRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminOrderService {

    private final AdminOrderRepository adminOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    public AdminOrderService(AdminOrderRepository adminOrderRepository,
                             OrderItemRepository orderItemRepository,
                             ProductVariantRepository productVariantRepository,
                             UserRepository userRepository) {
        this.adminOrderRepository = adminOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
    }

    public List<AdminOrderSummaryResponse> getAdminOrders(String keyword, String status) {
        String normalizedKeyword = normalize(keyword);
        String normalizedStatus = normalize(status);

        return adminOrderRepository.searchAdminOrders(normalizedKeyword, normalizedStatus)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public AdminOrderDetailResponse getAdminOrderDetail(Long orderId) {
        AdminOrderProjection order = adminOrderRepository.findAdminOrderDetail(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        List<AdminOrderItemResponse> items = orderItemRepository.findByOrder_Id(orderId)
                .stream()
                .map(this::toItemResponse)
                .toList();

        return toDetailResponse(order, items);
    }

    @Transactional
    public AdminOrderDetailResponse confirmOrderByAdmin(Long orderId, Long adminId) {
        requireAdmin(adminId);

        ShopOrder order = adminOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new RuntimeException("Chỉ có thể xác nhận đơn hàng đang chờ xử lý");
        }

        adminOrderRepository.confirmOrder(orderId, adminId);
        return getAdminOrderDetail(orderId);
    }

    @Transactional
    public AdminOrderDetailResponse cancelOrderByAdmin(Long orderId, Long adminId, String cancelReason) {
        requireAdmin(adminId);

        if (cancelReason == null || cancelReason.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do hủy đơn");
        }

        ShopOrder order = adminOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if ("CANCELLED".equals(order.getOrderStatus())) {
            throw new RuntimeException("Đơn hàng đã được hủy trước đó");
        }

        if ("COMPLETED".equals(order.getOrderStatus())) {
            throw new RuntimeException("Không thể hủy đơn hàng đã hoàn thành");
        }

        if (!("PENDING".equals(order.getOrderStatus()) || "CONFIRMED".equals(order.getOrderStatus()))) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng chờ xử lý hoặc đã xác nhận");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(orderId);

        for (OrderItem item : orderItems) {
            ProductVariant variant = item.getVariant();
            if (variant != null) {
                variant.setQuantity(variant.getQuantity() + item.getQuantity());
                productVariantRepository.save(variant);
            }
        }

        adminOrderRepository.cancelOrderByAdmin(orderId, adminId, cancelReason.trim());
        return getAdminOrderDetail(orderId);
    }

    private void requireAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin"));

        if (admin.getRole() == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new RuntimeException("Tài khoản không có quyền ADMIN");
        }
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private AdminOrderSummaryResponse toSummaryResponse(AdminOrderProjection order) {
        return new AdminOrderSummaryResponse(
                order.getId(),
                order.getUserId(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getConfirmedByAdminId(),
                order.getConfirmedByAdminName(),
                order.getCancelledByAdminId(),
                order.getCancelledByAdminName(),
                order.getCancelReason()
        );
    }

    private AdminOrderDetailResponse toDetailResponse(AdminOrderProjection order, List<AdminOrderItemResponse> items) {
        return new AdminOrderDetailResponse(
                order.getId(),
                order.getUserId(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getDeliveryAddress(),
                order.getTotalProductPrice(),
                order.getShippingFee(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getOrderStatus(),
                order.getNote(),
                order.getCreatedAt(),
                order.getConfirmedByAdminId(),
                order.getConfirmedByAdminName(),
                order.getConfirmedAt(),
                order.getCancelledByAdminId(),
                order.getCancelledByAdminName(),
                order.getCancelledAt(),
                order.getCancelReason(),
                items
        );
    }

    private AdminOrderItemResponse toItemResponse(OrderItem item) {
        return new AdminOrderItemResponse(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getVariant() != null ? item.getVariant().getId() : null,
                item.getProductName(),
                item.getSize(),
                item.getColor(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
