package com.ecommerce_backend.repository;

import com.ecommerce_backend.model.Cart;
import com.ecommerce_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
