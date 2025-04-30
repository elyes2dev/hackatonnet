package com.esprit.pi.controllers;

import com.esprit.pi.entities.ProjectEvaluation;
import com.esprit.pi.services.ProjectEvaluationService;
import com.esprit.pi.services.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project-evaluations")
public class ProjectEvaluationController {

    @Autowired
    private ProjectEvaluationService projectEvaluationService;

    @Autowired
    private EmailService emailService;

    // CREATE
    @PostMapping
    public ResponseEntity<String> createProjectEvaluation(@RequestBody ProjectEvaluation evaluation) {
        try {
            System.out.println("Reçu pour création : " + evaluation);
            ProjectEvaluation createdEvaluation = projectEvaluationService.createProjectEvaluation(evaluation);

            String teamMemberEmail = null;
            if (createdEvaluation.getTeamSubmission() != null &&
                    createdEvaluation.getTeamSubmission().getTeamMember() != null &&
                    createdEvaluation.getTeamSubmission().getTeamMember().getUser() != null) {
                teamMemberEmail = createdEvaluation.getTeamSubmission().getTeamMember().getUser().getEmail();
            }

            if (teamMemberEmail != null && !teamMemberEmail.trim().isEmpty()) {
                String subject = "Évaluation de votre projet";
                String body = "Bonjour,\nVotre projet '" + createdEvaluation.getTeamSubmission().getProjectName() +
                        "' a été évalué. Score : " + createdEvaluation.getScore() +
                        "\nFeedback : " + createdEvaluation.getFeedback();
                System.out.println("Tentative d'envoi d'email à : " + teamMemberEmail);
                emailService.sendSimpleEmail(teamMemberEmail, subject, body);
                System.out.println("Email envoyé avec succès à : " + teamMemberEmail);
            } else {
                System.out.println("Aucun email valide trouvé pour l'envoi.");
            }

            return new ResponseEntity<>("Évaluation créée avec succès", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur interne : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne : " + e.getMessage());
        }
    }

    // READ (tous)
    @GetMapping
    public ResponseEntity<List<ProjectEvaluation>> getAllProjectEvaluations() {
        List<ProjectEvaluation> evaluations = projectEvaluationService.getAllProjectEvaluations();
        System.out.println("Evaluations récupérées : " + evaluations);
        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    // READ (par ID)
    @GetMapping("/{id}")
    public ResponseEntity<ProjectEvaluation> getProjectEvaluationById(@PathVariable Long id) {
        Optional<ProjectEvaluation> evaluation = projectEvaluationService.getProjectEvaluationById(id);
        evaluation.ifPresent(e -> System.out.println("Evaluation par ID : " + e));
        return evaluation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProjectEvaluation(@PathVariable Long id, @RequestBody ProjectEvaluation evaluationDetails) {
        try {
            System.out.println("Reçu pour mise à jour : " + evaluationDetails);
            ProjectEvaluation updatedEvaluation = projectEvaluationService.updateProjectEvaluation(id, evaluationDetails);
            return new ResponseEntity<>(updatedEvaluation, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur interne : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne : " + e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectEvaluation(@PathVariable Long id) {
        try {
            projectEvaluationService.deleteProjectEvaluation(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("Erreur interne : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Add this method to your ProjectEvaluationController class
    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<List<ProjectEvaluation>> getEvaluationsBySubmissionId(@PathVariable Long submissionId) {
        try {
            List<ProjectEvaluation> evaluations = projectEvaluationService.getEvaluationsBySubmissionId(submissionId);

            // Break circular references to avoid JSON serialization issues
            evaluations.forEach(eval -> {
                if (eval.getTeamSubmission() != null) {
                    eval.getTeamSubmission().setEvaluations(null);
                }
            });

            return new ResponseEntity<>(evaluations, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des évaluations pour la soumission " + submissionId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<ProjectEvaluation>> getTopRatedProjects(@RequestParam Integer minScore) {
        try {
            List<ProjectEvaluation> topRated = projectEvaluationService.getTopRatedProjects(minScore);
            return new ResponseEntity<>(topRated, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des projets les mieux notés : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}