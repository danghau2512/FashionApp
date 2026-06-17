package com.example.fashionshopmobile.request;

public class UpdateCartItemRequest {

    private Integer quantity;

    public UpdateCartItemRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }
}