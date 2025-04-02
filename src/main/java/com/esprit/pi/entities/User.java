package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.jdbc.Work;

import java.util.Date;
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
    private String email;


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
    private List<Workshop> workshops;


    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hackathon> hackathons;  // This adds the relationship to Hackathons

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Manages forward serialization
    private SponsorApplication sponsorApplication; // One-to-One Relationship

    @OneToOne(mappedBy = "sponsor", cascade = CascadeType.ALL, orphanRemoval = true)
    private SponsorReward sponsorReward;

    // Many-to-many relationship with Team through TeamMembers
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMembers> teamMembers;

    private int monitorPoints = 0;

    @Enumerated(EnumType.STRING)
    private BadgeLevel badge = BadgeLevel.JUNIOR_COACH; // Default

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MonitorEvaluation> evaluations;

    public enum BadgeLevel {
        JUNIOR_COACH,
        ASSISTANT_COACH,
        SENIOR_COACH,
        HEAD_COACH,
        MASTER_MENTOR
    }
}