package com.example.fashionshop.service;

import com.example.fashionshop.dto.*;
import com.example.fashionshop.entity.*;
import com.example.fashionshop.integration.ghn.service.GhnShippingService;
import com.example.fashionshop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final GhnShippingService ghnShippingService;

    public OrderService(UserRepository userRepository, CartItemRepository cartItemRepository, ShopOrderRepository shopOrderRepository, OrderItemRepository orderItemRepository, ProductVariantRepository productVariantRepository, GhnShippingService ghnShippingService) {
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.ghnShippingService = ghnShippingService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<Long> cartItemIds = request.getCartItemIds()
                .stream()
                .distinct()
                .toList();

        List<CartItem> selectedCartItems = cartItemRepository
                .findByUser_IdAndIdIn(user.getId(), cartItemIds);

        if (selectedCartItems.size() != cartItemIds.size()) {
            throw new RuntimeException("Có sản phẩm không tồn tại hoặc không thuộc giỏ hàng của người dùng");
        }

        BigDecimal totalProductPrice = BigDecimal.ZERO;

        for (CartItem cartItem : selectedCartItems) {
            Product product = cartItem.getProduct();
            ProductVariant variant = cartItem.getVariant();

            if (!"ACTIVE".equals(product.getStatus())) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " hiện không hoạt động");
            }

            if (!"ACTIVE".equals(variant.getStatus())) {
                throw new RuntimeException("Biến thể sản phẩm không còn hoạt động");
            }

            if (cartItem.getQuantity() > variant.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ tồn kho");
            }

            BigDecimal price = product.getSalePrice() != null
                    ? product.getSalePrice()
                    : product.getPrice();

            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            totalProductPrice = totalProductPrice.add(subtotal);
        }

        ShippingQuoteRequest quoteRequest = new ShippingQuoteRequest(
                request.getUserId(),
                cartItemIds,
                request.getDeliveryDistrictId(),
                request.getDeliveryWardCode()
        );

        ShippingQuoteResponse quote =
                ghnShippingService.getQuote(quoteRequest);

        BigDecimal shippingFee = quote.getShippingFee();

        BigDecimal discountAmount = BigDecimal.ZERO;

        BigDecimal totalAmount = totalProductPrice
                .add(shippingFee)
                .subtract(discountAmount);

        ShopOrder order = new ShopOrder();
        order.setUser(user);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setTotalProductPrice(totalProductPrice);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "COD");
        order.setPaymentStatus("UNPAID");
        order.setOrderStatus("PENDING");
        order.setNote(request.getNote());

        ShopOrder savedOrder = shopOrderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : selectedCartItems) {
            Product product = cartItem.getProduct();
            ProductVariant variant = cartItem.getVariant();

            BigDecimal price = product.getSalePrice() != null
                    ? product.getSalePrice()
                    : product.getPrice();

            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setVariant(variant);
            orderItem.setProductName(product.getName());
            orderItem.setSize(variant.getSize());
            orderItem.setColor(variant.getColor());
            orderItem.setPrice(price);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);

            variant.setQuantity(variant.getQuantity() - cartItem.getQuantity());
            productVariantRepository.save(variant);
        }

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(selectedCartItems);

        return toOrderResponse(savedOrder, savedOrderItems);
    }
    public List<OrderSummaryResponse> getOrdersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        return shopOrderRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toOrderSummaryResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        ShopOrder order = shopOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(orderId);

        return toOrderResponse(order, orderItems);
    }

    private OrderSummaryResponse toOrderSummaryResponse(ShopOrder order) {
        OrderSummaryResponse response = new OrderSummaryResponse(
                order.getId(),
                order.getUser().getId(),
                order.getReceiverName(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getOrderStatus(),
                order.getCreatedAt()
        );

        List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(order.getId());

        if (orderItems != null && !orderItems.isEmpty()) {
            OrderItem firstItem = orderItems.get(0);

            if (firstItem.getProduct() != null) {
                response.setProductImageUrl(firstItem.getProduct().getImageUrl());
            }
        }

        return response;
    }
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        ShopOrder order = shopOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if ("CANCELLED".equals(order.getOrderStatus())) {
            throw new RuntimeException("Đơn hàng đã được hủy trước đó");
        }

        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang chờ xử lý");
        }

        if ("PAID".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Đơn hàng đã thanh toán, không được hủy");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrder_Id(orderId);

        for (OrderItem item : orderItems) {
            ProductVariant variant = item.getVariant();

            if (variant != null) {
                variant.setQuantity(variant.getQuantity() + item.getQuantity());
                productVariantRepository.save(variant);
            }
        }

        order.setOrderStatus("CANCELLED");
        order.setPaymentStatus("CANCELLED");

        ShopOrder savedOrder = shopOrderRepository.save(order);

        return toOrderResponse(savedOrder, orderItems);
    }

    private OrderResponse toOrderResponse(ShopOrder order, List<OrderItem> orderItems) {
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(this::toOrderItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
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
                itemResponses
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getVariant() != null ? item.getVariant().getId() : null,
                item.getProductName(),
                getOrderItemImageUrl(item),
                item.getSize(),
                item.getColor(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
    private String getOrderItemImageUrl(OrderItem item) {
        if (item.getVariant() != null
                && item.getVariant().getImageUrl() != null
                && !item.getVariant().getImageUrl().isBlank()) {
            return item.getVariant().getImageUrl();
        }

        if (item.getProduct() != null) {
            return item.getProduct().getImageUrl();
        }

        return null;
    }
}