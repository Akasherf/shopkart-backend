package com.ecommerce_backend.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}
