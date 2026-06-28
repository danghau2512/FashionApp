package com.example.fashionshop.service;

import com.example.fashionshop.dto.AddCartRequest;
import com.example.fashionshop.dto.CartItemResponse;
import com.example.fashionshop.dto.UpdateCartItemRequest;
import com.example.fashionshop.entity.CartItem;
import com.example.fashionshop.entity.Product;
import com.example.fashionshop.entity.ProductVariant;
import com.example.fashionshop.entity.User;
import com.example.fashionshop.repository.CartItemRepository;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.repository.ProductVariantRepository;
import com.example.fashionshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserProductEventService eventService;
    public CartService(CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository,
                       ProductVariantRepository productVariantRepository, UserProductEventService eventService) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.eventService = eventService;
    }

    public CartItemResponse addToCart(AddCartRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductVariant variant = productVariantRepository.findByIdAndStatus(request.getVariantId(), "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm"));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new RuntimeException("Sản phẩm hiện không hoạt động");
        }

        if (!variant.getProduct().getId().equals(product.getId())) {
            throw new RuntimeException("Biến thể không thuộc sản phẩm này");
        }

        CartItem cartItem = cartItemRepository
                .findByUser_IdAndVariant_Id(user.getId(), variant.getId())
                .orElse(null);

        int newQuantity;

        if (cartItem == null) {
            newQuantity = request.getQuantity();
        } else {
            newQuantity = cartItem.getQuantity() + request.getQuantity();
        }

        if (newQuantity > variant.getQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ");
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setVariant(variant);
        }

        cartItem.setQuantity(newQuantity);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        eventService.recordEvent(user.getId(), product.getId(), "ADD_TO_CART");

        return toCartItemResponse(savedCartItem);
    }

    public List<CartItemResponse> getCartByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        return cartItemRepository.findByUser_Id(userId)
                .stream()
                .map(this::toCartItemResponse)
                .toList();
    }

    public CartItemResponse updateCartItem(Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        ProductVariant variant = cartItem.getVariant();

        if (request.getQuantity() > variant.getQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ");
        }

        cartItem.setQuantity(request.getQuantity());

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        return toCartItemResponse(savedCartItem);
    }

    public void deleteCartItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCartByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        cartItemRepository.deleteByUser_Id(userId);
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        ProductVariant variant = cartItem.getVariant();

        BigDecimal price = product.getSalePrice() != null
                ? product.getSalePrice()
                : product.getPrice();

        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        String imageUrl = variant.getImageUrl() != null
                ? variant.getImageUrl()
                : product.getImageUrl();

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getUser().getId(),
                product.getId(),
                variant.getId(),
                product.getName(),
                imageUrl,
                variant.getSize(),
                variant.getColor(),
                price,
                cartItem.getQuantity(),
                variant.getQuantity(),
                subtotal
        );
    }
}