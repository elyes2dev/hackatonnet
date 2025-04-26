package com.esprit.pi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Model class to represent a mentor recommendation with compatibility score
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorRecommendation {
    private User mentor;
    private double compatibilityScore;
    private List<String> matchedSkills;

    // Convenience method to get formatted score as percentage
    public String getFormattedScore() {
        return String.format("%.1f%%", compatibilityScore * 100);
    }
}