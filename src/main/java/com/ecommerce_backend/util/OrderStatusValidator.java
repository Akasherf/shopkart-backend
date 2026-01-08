package com.ecommerce_backend.util;

import com.ecommerce_backend.model.OrderStatus;


public class OrderStatusValidator {

    public static boolean isValidTransition(
            OrderStatus current,
            OrderStatus next
    ) {
        return switch (current) {
            case CREATED ->
                    next == OrderStatus.CONFIRMED
                            || next == OrderStatus.CANCELLED;

            case PAYMENT_PENDING -> false;
            case PAID -> false;
            case CONFIRMED ->
                    next == OrderStatus.SHIPPED
                            || next == OrderStatus.CANCELLED;

            case SHIPPED ->
                    next == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
            case FAILED -> false;
        };
    }
}
