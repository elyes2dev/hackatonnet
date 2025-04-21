package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.jdbc.Work;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {

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
  //  @JsonIgnore  // Prevents recursion when serializing the Role entity
    @JsonIgnoreProperties("users")  // Ignore the 'users' field in Role to prevent recursion
    private Set<Role> roles = new HashSet<>();


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();


    @JsonIgnore  // This will prevent the workshops field from being serialized
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workshop> workshops = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hackathon> hackathons = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Manages forward serialization
    private SponsorApplication sponsorApplication;

    @OneToOne(mappedBy = "sponsor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private SponsorReward sponsorReward;

    // Many-to-many relationship with Team through TeamMembers
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents serialization of teamMembers
    private List<TeamMembers> teamMembers = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"user", "quiz"}) // Avoid circular references and unnecessary data
    private List<UserQuizScore> userQuizScores;  // List of quiz scores for the user

    private int monitorPoints = 0;

    @Enumerated(EnumType.STRING)
    private BadgeLevel badge = BadgeLevel.JUNIOR_COACH;

    @OneToMany(mappedBy = "mentor", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    @JsonIgnore
    private Set<MentorEvaluation> evaluations = new HashSet<>();


    // Add a specific reference to mentor application
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private MentorApplication mentorApplication;

    public void setMentorPoints(int totalPoints) {
        this.mentorPoints=totalPoints;
    }

    public void setBadge(BadgeLevel badgeLevel) {
        this.badge=badgeLevel;
    }

    public BadgeLevel getBadge() {
        return badge;
    }

   

    public enum BadgeLevel {
        JUNIOR_COACH,
        ASSISTANT_COACH,
        SENIOR_COACH,
        HEAD_COACH,
        MASTER_MENTOR
    }

    public String getPicture() {
        return picture;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

      public List<UserQuizScore> getUserQuizScores() {
        return userQuizScores;
    }

    public void setUserQuizScores(List<UserQuizScore> userQuizScores) {
        this.userQuizScores = userQuizScores;
    }

    public List<Workshop> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(List<Workshop> workshops) {
        this.workshops = workshops;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    private Integer mentorPoints;


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

        if (mentorPoints >= 200) {
            this.badge = BadgeLevel.MASTER_MENTOR;
        } else if (mentorPoints >= 150) {
            this.badge = BadgeLevel.HEAD_COACH;
        } else if (mentorPoints >= 100) {
            this.badge = BadgeLevel.SENIOR_COACH;
        } else if (mentorPoints >= 50) {
            this.badge = BadgeLevel.ASSISTANT_COACH;
        } else {
            this.badge = BadgeLevel.JUNIOR_COACH;
        }
    }

    public String getDescription() {
        return description;
    }

    public Integer getScore() {
        return score;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public List<Hackathon> getHackathons() {
        return hackathons;
    }

    public SponsorApplication getSponsorApplication() {
        return sponsorApplication;
    }

    public SponsorReward getSponsorReward() {
        return sponsorReward;
    }

    public List<TeamMembers> getTeamMembers() {
        return teamMembers;
    }

    public int getMonitorPoints() {
        return monitorPoints;
    }

    public Set<MentorEvaluation> getEvaluations() {
        return evaluations;
    }

    public MentorApplication getMentorApplication() {
        return mentorApplication;
    }

    public Integer getMentorPoints() {
        return mentorPoints;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public void setHackathons(List<Hackathon> hackathons) {
        this.hackathons = hackathons;
    }

    public void setSponsorApplication(SponsorApplication sponsorApplication) {
        this.sponsorApplication = sponsorApplication;
    }

    public void setSponsorReward(SponsorReward sponsorReward) {
        this.sponsorReward = sponsorReward;
    }

    public void setTeamMembers(List<TeamMembers> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public void setMonitorPoints(int monitorPoints) {
        this.monitorPoints = monitorPoints;
    }

    public void setEvaluations(Set<MentorEvaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public void setMentorApplication(MentorApplication mentorApplication) {
        this.mentorApplication = mentorApplication;
    }

    public void setMentorPoints(Integer mentorPoints) {
        this.mentorPoints = mentorPoints;
    }
}
