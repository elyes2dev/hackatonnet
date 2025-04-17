package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListMentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    @JsonIgnoreProperties({
            "hackathons", "workshops", "skills", "teamMembers", "password", "authorities", "userQuizScores",
            "sponsorApplication", "sponsorReward", "mentorApplication"
    })
    private User mentor;

    @ManyToOne
    @JoinColumn(name = "hackathon_id", nullable = false)
    @JsonIgnoreProperties({
            "teams", "prizes", "posts",
            "createdBy"
    })
    private Hackathon hackathon;

    private int numberOfTeams;
}