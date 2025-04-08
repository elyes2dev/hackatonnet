package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class PreviousExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    @JsonBackReference // This prevents serialization of the back reference
    private MentorApplication application;

    private int year;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String hackathonName;

    private int numberOfTeamsCoached;
}

