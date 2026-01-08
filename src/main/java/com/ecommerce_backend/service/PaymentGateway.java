package com.ecommerce_backend.service;

public interface PaymentGateway {

    String initiatePayment(double amount);

    boolean verifyPayment(String paymentRef);
}

