package com.ecommerce_backend.service;

import com.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce_backend.model.*;
import com.ecommerce_backend.repository.OrderRepository;
import com.ecommerce_backend.repository.PaymentRepository;
import com.ecommerce_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final PaymentGateway gateway;

    public PaymentServiceImpl(PaymentRepository paymentRepo,
                              OrderRepository orderRepo, ProductRepository productRepo, PaymentGateway gateway) {
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.gateway = gateway;
    }

    @Override
    public void processPayment(Long orderId, boolean success) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setCreatedAt(LocalDateTime.now());

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);
        }

        paymentRepo.save(payment);
        orderRepo.save(order);
    }
    @Override
    public void refundPayment(Long orderId) {

        Order order = orderRepo.findById(orderId).orElseThrow();
        Payment payment = paymentRepo.findByOrder(order).orElseThrow();

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Refund not allowed");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        order.setStatus(OrderStatus.CANCELLED);

        // restore stock
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
        }
    }
    public String initiatePayment(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Invalid order state");
        }

        String ref = gateway.initiatePayment(order.getTotalAmount());

        Payment payment = new Payment();
        payment.setPaymentRef(ref);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setOrder(order);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);

        order.setStatus(OrderStatus.PAYMENT_PENDING);
        orderRepo.save(order);

        return ref;
    }

    @Override
    @Transactional
    public void confirmPayment(String paymentRef) {

        // 1️⃣ Fetch payment safely
        Payment payment = paymentRepo.findByPaymentRef(paymentRef)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        // 2️⃣ Idempotency: already processed
        if (payment.getStatus() == PaymentStatus.SUCCESS ||
                payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        Order order = payment.getOrder();

        // 3️⃣ Verify with payment gateway
        boolean verified = gateway.verifyPayment(paymentRef);

        if (!verified) {
            // 4️⃣ Handle failed payment
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.FAILED);

            paymentRepo.save(payment);
            orderRepo.save(order);
            return;
        }

        // 5️⃣ Handle successful payment
        payment.setStatus(PaymentStatus.SUCCESS);
        order.setStatus(OrderStatus.PAID);

        paymentRepo.save(payment);
        orderRepo.save(order);
    }


}

