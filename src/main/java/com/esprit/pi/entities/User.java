package com.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.jdbc.Work;

import java.util.ArrayList;
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
    private String username;
    private String password;

    @Temporal(TemporalType.DATE)
    private Date birthdate;
    private String picture;
    private String description;
    private Integer score;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToMany(cascade = CascadeType.PERSIST)
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
    private List<Workshop> workshops = new ArrayList<>(); // Fixed initialization


    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hackathon> hackathons;  // This adds the relationship to Hackathons

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SponsorApplication sponsorApplication; // One-to-One Relationship

    // Many-to-many relationship with Team through TeamMembers
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMembers> teamMembers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserQuizScore> userQuizScores;  // List of quiz scores for the user

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


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public List<Workshop> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(List<Workshop> workshops) {
        this.workshops = workshops;
    }

    public List<UserQuizScore> getUserQuizScores() {
        return userQuizScores;
    }

    public void setUserQuizScores(List<UserQuizScore> userQuizScores) {
        this.userQuizScores = userQuizScores;
    }
}