package com.ecommerce_backend.controller;

import com.ecommerce_backend.dto.CartResponse;
import com.ecommerce_backend.model.Cart;
import com.ecommerce_backend.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public void add(@RequestParam Long productId,
                    @RequestParam int quantity,
                    Authentication auth) {
        cartService.addToCart(auth.getName(), productId, quantity);
    }

    @GetMapping
    public CartResponse view(Authentication auth) {
        return cartService.viewCart(auth.getName());
    }

    @DeleteMapping("/remove")
    public void remove(
            @RequestParam Long productId,
            Authentication auth) {
        cartService.removeFromCart(auth.getName(), productId);
    }


}
