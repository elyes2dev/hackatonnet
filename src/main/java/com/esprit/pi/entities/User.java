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
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.jdbc.Work;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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
    private List<Workshop> workshops = new ArrayList<>(); // Fixed initialization

    @JsonIgnore
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"user", "quiz"}) // Avoid circular references and unnecessary data
    private List<UserQuizScore> userQuizScores;  // List of quiz scores for the user

    private int monitorPoints = 0;

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
}