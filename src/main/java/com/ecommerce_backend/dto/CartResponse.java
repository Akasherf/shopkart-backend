package com.ecommerce_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private double totalAmount;
}
