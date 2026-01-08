package com.ecommerce_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;
    private double totalAmount;
    private String status;
    private List<OrderItemResponse> items;
}
