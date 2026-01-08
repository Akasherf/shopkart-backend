package com.ecommerce_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentRef; // gateway ref / mock id

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private double amount;


    private LocalDateTime createdAt;

    @OneToOne
    private Order order;


}

