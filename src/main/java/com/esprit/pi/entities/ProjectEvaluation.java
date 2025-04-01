package com.esprit.pi.entities;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private TeamSubmission teamSubmission;  // Foreign key to TeamSubmission (Many-to-One relationship)

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "evaluator_id", referencedColumnName = "id", nullable = false)
    private User evaluator;  // Foreign key to User (Many-to-One relationship)

    private Integer score;  // Evaluation score

    private String feedback;  // Feedback from evaluator

    @Temporal(TemporalType.TIMESTAMP)
    private Date evaluationDate;  // Date of the evaluation

}
