package com.ecommerce_backend.service;

import org.springframework.stereotype.Service;

@Service
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public String initiatePayment(double amount) {
        return "PAY_" + System.currentTimeMillis();
    }

    @Override
    public boolean verifyPayment(String paymentRef) {
        return true; // always success (mock)
    }
}
