package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false, updatable = false)
    @JsonIgnoreProperties({
            "evaluations", "hackathons", "workshops", "skills", "teamMembers",
            "password", "authorities", "userQuizScores", "sponsorApplication",
            "sponsorReward", "mentorApplication"
    })
    private User mentor;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false, updatable = false)
    @JsonIgnoreProperties({"hackathon", "teamMembers", "projectSubmissions"})
    private Team team;

    @Column(nullable = false)
    private int rating; // From 1 to 5



    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


}