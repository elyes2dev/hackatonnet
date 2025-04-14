package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User  {

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
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_skill",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workshop> workshops = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hackathon> hackathons = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SponsorApplication sponsorApplication;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents serialization of teamMembers
    private List<TeamMembers> teamMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserQuizScore> userQuizScores = new ArrayList<>();

    private int monitorPoints = 0;

    @Enumerated(EnumType.STRING)
    private BadgeLevel badge = BadgeLevel.JUNIOR_COACH;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MonitorEvaluation> evaluations = new HashSet<>();

    public enum BadgeLevel {
        JUNIOR_COACH,
        ASSISTANT_COACH,
        SENIOR_COACH,
        HEAD_COACH,
        MASTER_MENTOR
    }



    // Getters and Setters (unchanged)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Date getBirthdate() { return birthdate; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public Set<Skill> getSkills() { return skills; }
    public void setSkills(Set<Skill> skills) { this.skills = skills; }
    public List<Workshop> getWorkshops() { return workshops; }
    public void setWorkshops(List<Workshop> workshops) { this.workshops = workshops; }
    public List<Hackathon> getHackathons() { return hackathons; }
    public void setHackathons(List<Hackathon> hackathons) { this.hackathons = hackathons; }
    public SponsorApplication getSponsorApplication() { return sponsorApplication; }
    public void setSponsorApplication(SponsorApplication sponsorApplication) { this.sponsorApplication = sponsorApplication; }
    public List<TeamMembers> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMembers> teamMembers) { this.teamMembers = teamMembers; }
    public List<UserQuizScore> getUserQuizScores() { return userQuizScores; }
    public void setUserQuizScores(List<UserQuizScore> userQuizScores) { this.userQuizScores = userQuizScores; }
    public int getMonitorPoints() { return monitorPoints; }
    public void setMonitorPoints(int monitorPoints) { this.monitorPoints = monitorPoints; }
    public BadgeLevel getBadge() { return badge; }
    public void setBadge(BadgeLevel badge) { this.badge = badge; }
    public Set<MonitorEvaluation> getEvaluations() { return evaluations; }
    public void setEvaluations(Set<MonitorEvaluation> evaluations) { this.evaluations = evaluations; }
}