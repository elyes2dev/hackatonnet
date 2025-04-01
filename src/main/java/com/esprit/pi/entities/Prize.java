package com.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User sponsor; // The sponsor giving the prize

    @ManyToOne(cascade = CascadeType.ALL)
    private Hackathon hackathon; // The hackathon receiving the prize

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrizeType prizeType; // MONEY or PRODUCT

    private BigDecimal amount; // Only if prizeType = MONEY

    private String productName; // Only if prizeType = PRODUCT

    @Column(columnDefinition = "TEXT")
    private String productDescription; // Only if prizeType = PRODUCT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;

    public enum PrizeType {
        MONEY, PRODUCT
    }
}