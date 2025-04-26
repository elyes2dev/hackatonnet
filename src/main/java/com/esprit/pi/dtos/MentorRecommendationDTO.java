package com.esprit.pi.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorRecommendationDTO {
    private Long mentorId;
    private String mentorName;
    private double matchScore;
    private List<String> matchingSkills;
}