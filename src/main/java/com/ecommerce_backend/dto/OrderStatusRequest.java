package com.ecommerce_backend.dto;

import com.ecommerce_backend.model.OrderStatus;
import lombok.Data;
import lombok.Getter;

@Data
public class OrderStatusRequest {
    private OrderStatus status;

}
