package com.ecommerce_backend.controller;

import com.ecommerce_backend.dto.ApiResponse;
import com.ecommerce_backend.dto.OrderStatusRequest;
import com.ecommerce_backend.model.OrderStatusHistory;
import com.ecommerce_backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Orders", description = "Order management APIs")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ================= ADMIN UPDATE STATUS =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusRequest request,
            Authentication auth
    ) {
        orderService.updateOrderStatus(
                id,
                request.getStatus(),
                auth.getName()
        );

        return new ApiResponse<>(
                true,
                "Order status updated successfully",
                null
        );
    }

    // ================= USER CANCEL ORDER =================
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancelOrder(
            @PathVariable Long id,
            Authentication auth
    ) {
        orderService.cancelOrder(id, auth.getName());

        return new ApiResponse<>(
                true,
                "Order cancelled successfully",
                null
        );
    }

    // ================= ORDER HISTORY =================
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}/history")
    public ApiResponse<List<OrderStatusHistory>> history(
            @PathVariable Long id,
            Authentication auth
    ) {
        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return new ApiResponse<>(
                true,
                "Order history fetched",
                orderService.getOrderHistory(id, auth.getName(), isAdmin)
        );
    }

    @Operation(summary = "Place order", description = "Places order for logged-in user")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ApiResponse<Void> placeOrder(Authentication auth) {
        orderService.placeOrder(auth.getName());
        return new ApiResponse<>(true, "Order placed successfully", null);
    }

}
