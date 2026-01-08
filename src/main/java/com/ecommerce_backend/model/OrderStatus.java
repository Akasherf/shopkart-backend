package com.ecommerce_backend.model;

public enum OrderStatus {
    CREATED,        // order created but not paid
    PAYMENT_PENDING,
    PAID,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED
}
