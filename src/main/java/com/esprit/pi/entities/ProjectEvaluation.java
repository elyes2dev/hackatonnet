package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProjectEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private TeamSubmission teamSubmission;

    @ManyToOne
    @JoinColumn(name = "evaluator_id", referencedColumnName = "id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Permet la désérialisation mais pas la sérialisation
    private User evaluator;

    private Integer score;
    private String feedback;

    @Temporal(TemporalType.TIMESTAMP)
    private Date evaluationDate;

    // Getters et setters...
    public String getProjectName() {
        return teamSubmission != null ? teamSubmission.getProjectName() : null;
    }
}