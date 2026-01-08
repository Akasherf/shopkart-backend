package com.ecommerce_backend.service;

public interface PaymentService {
    public void processPayment(Long orderId, boolean success);
    public void refundPayment(Long orderId);
    public void confirmPayment(String paymentRef);
    public String initiatePayment(Long orderId);
}
