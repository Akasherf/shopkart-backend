package com.ecommerce_backend.controller;

import com.ecommerce_backend.dto.ApiResponse;
import com.ecommerce_backend.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}")
    public void makePayment(
            @PathVariable Long orderId,
            @RequestParam boolean success
    ) {
        paymentService.processPayment(orderId, success);
    }

    @PostMapping("/{orderId}/initiate")
    public ApiResponse<String> initiate(@PathVariable Long orderId) {
        return new ApiResponse<>(
                true,
                "Payment initiated",
                paymentService.initiatePayment(orderId)
        );
    }

    @PostMapping("/{paymentRef}/confirm")
    public ApiResponse<Void> confirm(@PathVariable String paymentRef) {
        paymentService.confirmPayment(paymentRef);
        return new ApiResponse<>(true, "Payment confirmed", null);
    }
}

