package com.ecommerce_backend.dto;

public class PaymentWebhookRequest {

    private String paymentRef;

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }
}

