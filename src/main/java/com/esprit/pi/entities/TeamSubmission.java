package com.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeamSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonBackReference // Relation avec TeamMembers (inchangée, gérée séparément)
    @JoinColumn(name = "team_member_id", referencedColumnName = "id", nullable = true)
    private TeamMembers teamMember;

    private String projectName;
    private String description;
    private String repoLink;
    private LocalDateTime submissionDate;
    @OneToMany(mappedBy = "teamSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectEvaluation> evaluations = new ArrayList<>();

    // Getters et setters
    public Long getId() {
        return id;
    }

    public TeamMembers getTeamMember() {
        return teamMember;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public String getRepoLink() {
        return repoLink;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public List<ProjectEvaluation> getEvaluations() {
        return evaluations;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTeamMember(TeamMembers teamMember) {
        this.teamMember = teamMember;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRepoLink(String repoLink) {
        this.repoLink = repoLink;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setEvaluations(List<ProjectEvaluation> evaluations) {
        this.evaluations = evaluations;
    }
}

