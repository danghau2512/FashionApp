package com.example.fashionshopmobile.request;

public class AddCartRequest {

    private Long userId;
    private Long productId;
    private Long variantId;
    private Integer quantity;

    public AddCartRequest(Long userId, Long productId, Long variantId, Integer quantity) {
        this.userId = userId;
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
    }

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