package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class SponsorReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JsonBackReference
    private User sponsor; // Links to the User (Sponsor)

    private int reputationPoints = 0; // Total earned points

    @Enumerated(EnumType.STRING)
    private SponsorBadge badge = SponsorBadge.BRONZE; // Default starting badge

    private LocalDateTime unlockedAt = LocalDateTime.now(); // When badge was updated

    public enum SponsorBadge {
        BRONZE, SILVER, GOLD, PLATINUM
    }

    // Method to determine the correct badge based on points
    public SponsorBadge calculateBadge() {
        if (reputationPoints >= 50) return SponsorBadge.PLATINUM;
        if (reputationPoints >= 25) return SponsorBadge.GOLD;
        if (reputationPoints > 10) return SponsorBadge.SILVER;
        return SponsorBadge.BRONZE;
    }
}
