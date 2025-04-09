package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastname;

    @Temporal(TemporalType.DATE)
    private Date birthdate;
    private String picture;
    private String description;
    private Integer score;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @ManyToMany
    @JoinTable(
            name = "user_skill",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Workshop> workshops;


    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Hackathon> hackathons;  // This adds the relationship to Hackathons

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private SponsorApplication sponsorApplication; // One-to-One Relationship

    // Many-to-many relationship with Team through TeamMembers
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TeamMembers> teamMembers;

    private Integer mentorPoints;

    @Enumerated(EnumType.STRING)
    private BadgeLevel badge = BadgeLevel.JUNIOR_COACH; // Default

    @OneToMany(mappedBy = "mentor", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    @JsonIgnore
    private Set<MentorEvaluation> evaluations = new HashSet<>();


    // Add a specific reference to mentor application
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private MentorApplication mentorApplication;



    public enum BadgeLevel {
        JUNIOR_COACH,
        ASSISTANT_COACH,
        SENIOR_COACH,
        HEAD_COACH,
        MASTER_MENTOR
    }


    public void calculateMentorPoints() {
        if (evaluations == null || evaluations.isEmpty()) {
            this.mentorPoints = 0;
            this.badge = BadgeLevel.JUNIOR_COACH;
            return;
        }

        // Calculate total points based on evaluations
        int totalPoints = evaluations.stream()
                .mapToInt(eval -> {
                    // Base points for being evaluated
                    int basePoints = 10;
                    // Bonus points based on rating (1-5 stars)
                    int ratingBonus = eval.getRating() * 2;
                    return basePoints + ratingBonus;
                })
                .sum();

        this.mentorPoints = totalPoints;
        updateBadgeLevel();
    }

    private void updateBadgeLevel() {
        if (mentorPoints == null) {
            this.badge = BadgeLevel.JUNIOR_COACH;
            return;
        }

        if (mentorPoints >= 20) {
            this.badge = BadgeLevel.MASTER_MENTOR;
        } else if (mentorPoints >= 15) {
            this.badge = BadgeLevel.HEAD_COACH;
        } else if (mentorPoints >= 10) {
            this.badge = BadgeLevel.SENIOR_COACH;
        } else if (mentorPoints >= 5) {
            this.badge = BadgeLevel.ASSISTANT_COACH;
        } else {
            this.badge = BadgeLevel.JUNIOR_COACH;
        }
    }

}