package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne
    @JsonIgnore // Prevent serialization of the full User object
    private User sponsor; // The sponsor giving the prize

    @ManyToOne
    @JsonIgnore // Prevent serialization of the full User object
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrizeCategory prizeCategory; // New field for categorization

    public enum PrizeType {
        MONEY, PRODUCT
    }

    public enum PrizeCategory {
        BEST_INNOVATION, BEST_DESIGN, BEST_AI_PROJECT
    }
}