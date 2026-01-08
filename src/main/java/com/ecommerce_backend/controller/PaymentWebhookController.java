package com.ecommerce_backend.controller;

import com.ecommerce_backend.dto.ApiResponse;
import com.ecommerce_backend.dto.PaymentWebhookRequest;
import com.ecommerce_backend.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private final PaymentService paymentService;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 🔔 Gateway callback
    @PostMapping("/webhook")
    public ApiResponse<Void> handleWebhook(
            @RequestBody PaymentWebhookRequest request
    ) {
        paymentService.confirmPayment(request.getPaymentRef());
        return new ApiResponse<>(true, "Payment processed", null);
    }
}

