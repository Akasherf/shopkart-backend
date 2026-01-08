package com.ecommerce_backend.service;

import com.ecommerce_backend.dto.CartItemResponse;
import com.ecommerce_backend.dto.CartResponse;
import com.ecommerce_backend.model.Cart;
import com.ecommerce_backend.model.CartItem;
import com.ecommerce_backend.model.Product;
import com.ecommerce_backend.model.User;
import com.ecommerce_backend.repository.CartRepository;
import com.ecommerce_backend.repository.ProductRepository;
import com.ecommerce_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public CartServiceImpl(CartRepository cartRepo,
                           ProductRepository productRepo,
                           UserRepository userRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void addToCart(String email, Long productId, int quantity) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            return cartRepo.save(newCart);
        });

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartRepo.save(cart);
                return;
            }
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);

        cart.getItems().add(item);
        cartRepo.save(cart);
    }

    @Override
    public CartResponse viewCart(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        Cart cart = cartRepo.findByUser(user).orElseThrow();

        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> {
                    CartItemResponse dto = new CartItemResponse();
                    dto.setProductId(item.getProduct().getId());
                    dto.setProductName(item.getProduct().getName());
                    dto.setPrice(item.getProduct().getPrice());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                }).toList();

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(items);
        response.setTotalAmount(
                items.stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum()
        );

        return response;
    }


    @Override
    public void removeFromCart(String email, Long productId) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(
                item -> item.getProduct().getId().equals(productId)
        );

        cartRepo.save(cart);
    }

}
