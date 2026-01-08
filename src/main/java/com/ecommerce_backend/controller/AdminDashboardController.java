package com.ecommerce_backend.controller;

import com.ecommerce_backend.dto.AdminOrderDashboardResponse;
import com.ecommerce_backend.dto.ApiResponse;
import com.ecommerce_backend.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final OrderService orderService;

    public AdminDashboardController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ApiResponse<AdminOrderDashboardResponse> getOrderDashboard() {
        return new ApiResponse<>(
                true,
                "Dashboard data fetched",
                orderService.getDashboardCounts()
        );
    }
}
