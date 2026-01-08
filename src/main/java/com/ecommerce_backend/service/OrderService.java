package com.ecommerce_backend.service;

import com.ecommerce_backend.dto.AdminOrderDashboardResponse;
import com.ecommerce_backend.dto.OrderResponse;
import com.ecommerce_backend.model.Order;
import com.ecommerce_backend.model.OrderStatus;
import com.ecommerce_backend.model.OrderStatusHistory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    void placeOrder(String email);

    List<OrderResponse> getOrders(String email);

    void updateOrderStatus(
            Long orderId,
            OrderStatus newStatus,
            String updatedBy
    );

    void cancelOrder(Long orderId, String email);

    List<OrderStatusHistory> getOrderHistory(Long orderId,String email, boolean isAdmin);

    Page<OrderResponse> getAllOrders(
            int page,
            int size,
            String sortBy,
            String direction
    );

    Order getOrderById(Long orderId);

    AdminOrderDashboardResponse getDashboardCounts();
}
