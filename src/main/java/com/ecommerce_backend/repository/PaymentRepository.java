package com.ecommerce_backend.repository;

import com.ecommerce_backend.model.Order;
import com.ecommerce_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByPaymentRef(String paymentRef);

}
