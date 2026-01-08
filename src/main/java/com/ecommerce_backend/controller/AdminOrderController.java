package com.ecommerce_backend.controller;

import com.ecommerce_backend.dto.ApiResponse;
import com.ecommerce_backend.dto.OrderResponse;
import com.ecommerce_backend.dto.OrderStatusRequest;
import com.ecommerce_backend.dto.PageResponse;
import com.ecommerce_backend.model.Order;
import com.ecommerce_backend.model.OrderStatusHistory;
import com.ecommerce_backend.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1️⃣ VIEW ALL ORDERS (PAGINATED)
    @GetMapping
    public ApiResponse<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Page<OrderResponse> pageData =
                orderService.getAllOrders(page, size, sortBy, direction);

        PageResponse<OrderResponse> response = new PageResponse<>();
        response.setContent(pageData.getContent());
        response.setPage(pageData.getNumber());
        response.setSize(pageData.getSize());
        response.setTotalElements(pageData.getTotalElements());
        response.setTotalPages(pageData.getTotalPages());
        response.setLast(pageData.isLast());

        return new ApiResponse<>(
                true,
                "Orders fetched successfully",
                response
        );
    }



    // 2️⃣ VIEW ORDER DETAILS
    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderById(@PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Order details fetched",
                orderService.getOrderById(id)

        );
    }

    // 3️⃣ UPDATE ORDER STATUS
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
                "Order status updated",
                null

        );
    }

    // 4️⃣ VIEW ORDER HISTORY
    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<List<OrderStatusHistory>> getHistory(
            @PathVariable Long id,
            Authentication auth
    ) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return new ApiResponse<>(
                true,
                "Order history fetched",
                orderService.getOrderHistory(id, auth.getName(), isAdmin)

        );
    }

}
