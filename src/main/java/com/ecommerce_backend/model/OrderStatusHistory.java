package com.ecommerce_backend.model;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    private Order order;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus fromStatus;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus toStatus;

    @Setter
    private String changedBy;

    @Setter
    private LocalDateTime changedAt;



}
