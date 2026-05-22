package com.example.fashionshop.controller;

import com.example.fashionshop.dto.AddCartRequest;
import com.example.fashionshop.dto.CartItemResponse;
import com.example.fashionshop.dto.UpdateCartItemRequest;
import com.example.fashionshop.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public CartItemResponse addToCart(@Valid @RequestBody AddCartRequest request) {
        return cartService.addToCart(request);
    }

    @GetMapping("/user/{userId}")
    public List<CartItemResponse> getCartByUserId(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
    }

    @PutMapping("/{cartItemId}")
    public CartItemResponse updateCartItem(@PathVariable Long cartItemId,
                                           @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateCartItem(cartItemId, request);
    }

    @DeleteMapping("/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
    }

    @DeleteMapping("/user/{userId}")
    public void clearCartByUserId(@PathVariable Long userId) {
        cartService.clearCartByUserId(userId);
    }
}