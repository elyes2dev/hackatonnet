package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Remove the redundant mentorId field
    // private Long mentorId;  -- REMOVE THIS

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({
            "mentorApplication", "hackathons", "workshops", "skills", "teamMembers",
            "evaluations", "password", "authorities", "userQuizScores",
            "sponsorApplication", "sponsorReward"
    })
    private User user;


    @Column(columnDefinition = "TEXT")
    private String applicationText;

    @Column(nullable = true)
    private String cv;

    @Column(nullable = true)
    private String uploadPaper;

    @ElementCollection
    @CollectionTable(name = "mentor_application_links",
            joinColumns = @JoinColumn(name = "mentor_application_id"))
    @Column(name = "link")
    @OnDelete(action = OnDeleteAction.CASCADE) // Hibernate specific annotation
    private List<String> links;

    private boolean hasPreviousExperience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PreviousExperience> previousExperiences;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;



}