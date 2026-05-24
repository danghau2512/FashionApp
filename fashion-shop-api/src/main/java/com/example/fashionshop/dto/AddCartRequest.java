package com.example.fashionshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddCartRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Product ID không được để trống")
    private Long productId;

    @NotNull(message = "Variant ID không được để trống")
    private Long variantId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getVariantId() {
        return variantId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}