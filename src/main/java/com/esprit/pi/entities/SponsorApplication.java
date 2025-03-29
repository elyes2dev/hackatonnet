package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SponsorApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private User user; // The user who applied

    private String companyName;

    private String companyLogo; // File path to logo

    private String documentPath; // File path to business document

    @Column(nullable = false, unique = true)
    private int registrationNumber; // Business registration number

    private String websiteUrl;

    private boolean notified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;

}
