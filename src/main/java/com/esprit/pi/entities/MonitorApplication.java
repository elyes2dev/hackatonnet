package com.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class MonitorApplication {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Long mentorId;

        @Column(columnDefinition = "TEXT")
        private String applicationText;

        private String cv;

        private String uploadPaper;

        @ElementCollection
        private List<String> links;

        private boolean hasPreviousExperience;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ApplicationStatus status = ApplicationStatus.PENDING;

        @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<PreviousExperience> previousExperiences;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        private Date createdAt;
    }