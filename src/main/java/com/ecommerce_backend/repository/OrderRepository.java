package com.ecommerce_backend.repository;

import com.ecommerce_backend.model.Cart;
import com.ecommerce_backend.model.Order;
import com.ecommerce_backend.model.OrderStatus;
import com.ecommerce_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    long countByStatus(OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o")
    long countAllOrders();
}
