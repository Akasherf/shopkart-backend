package com.ecommerce_backend.service;

import com.ecommerce_backend.dto.CartResponse;
import com.ecommerce_backend.model.Cart;

public interface CartService {
    void addToCart(String email, Long productId, int quantity);
    CartResponse viewCart(String email);
    void removeFromCart(String email, Long productId);
}
