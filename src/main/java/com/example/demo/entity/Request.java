package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who made the request
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Product requested
    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Double quantiteDemandee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime dateRequest = LocalDateTime.now();

    private LocalDateTime dateValidation;

    // Admin who validated/rejected the request
    @ManyToOne
    @JoinColumn(name = "validated_by_admin_id")
    private Admin validatedBy;

}
