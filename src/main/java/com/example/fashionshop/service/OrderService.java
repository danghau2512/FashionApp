package com.example.fashionshop.service;

import com.example.fashionshop.dto.CreateOrderRequest;
import com.example.fashionshop.dto.OrderItemResponse;
import com.example.fashionshop.dto.OrderResponse;
import com.example.fashionshop.entity.*;
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

    public OrderService(UserRepository userRepository,
                        CartItemRepository cartItemRepository,
                        ShopOrderRepository shopOrderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductVariantRepository productVariantRepository) {
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productVariantRepository = productVariantRepository;
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

        BigDecimal shippingFee = request.getShippingFee() != null
                ? request.getShippingFee()
                : BigDecimal.ZERO;

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
                item.getSize(),
                item.getColor(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}