package com.esprit.pi.services;

import com.esprit.pi.entities.ProjectEvaluation;
import com.esprit.pi.entities.TeamSubmission;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.ProjectEvaluationRepository;
import com.esprit.pi.repositories.TeamSubmissionRepository;
import com.esprit.pi.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectEvaluationService {

    @Autowired
    private ProjectEvaluationRepository projectEvaluationRepository;

    @Autowired
    private TeamSubmissionRepository teamSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    // CREATE
    public ProjectEvaluation createProjectEvaluation(ProjectEvaluation evaluation) {
        // Vérifier teamSubmission
        if (evaluation.getTeamSubmission() == null || evaluation.getTeamSubmission().getId() == null) {
            throw new IllegalArgumentException("TeamSubmission est obligatoire et doit avoir un ID valide.");
        }
        TeamSubmission teamSubmission = teamSubmissionRepository.findById(evaluation.getTeamSubmission().getId())
                .orElseThrow(() -> new IllegalArgumentException("TeamSubmission avec ID " + evaluation.getTeamSubmission().getId() + " non trouvé."));
        evaluation.setTeamSubmission(teamSubmission);

        // Vérifier evaluator
        if (evaluation.getEvaluator() == null || evaluation.getEvaluator().getId() == null) {
            throw new IllegalArgumentException("L'évaluateur est obligatoire et doit avoir un ID valide.");
        }
        User evaluator = userRepository.findById(evaluation.getEvaluator().getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec ID " + evaluation.getEvaluator().getId() + " non trouvé."));
        evaluation.setEvaluator(evaluator);

        return projectEvaluationRepository.save(evaluation);
    }

    // READ (tous)
    public List<ProjectEvaluation> getAllProjectEvaluations() {
        return projectEvaluationRepository.findAll();
    }

    // READ (par ID)
    public Optional<ProjectEvaluation> getProjectEvaluationById(Long id) {
        return projectEvaluationRepository.findById(id);
    }

    // UPDATE
    public ProjectEvaluation updateProjectEvaluation(Long id, ProjectEvaluation evaluationDetails) {
        Optional<ProjectEvaluation> optionalEvaluation = projectEvaluationRepository.findById(id);
        if (optionalEvaluation.isPresent()) {
            ProjectEvaluation existingEvaluation = optionalEvaluation.get();

            // Mettre à jour les champs simples
            if (evaluationDetails.getScore() != null) {
                existingEvaluation.setScore(evaluationDetails.getScore());
            }
            if (evaluationDetails.getFeedback() != null) {
                existingEvaluation.setFeedback(evaluationDetails.getFeedback());
            }
            if (evaluationDetails.getEvaluationDate() != null) {
                existingEvaluation.setEvaluationDate(evaluationDetails.getEvaluationDate());
            }

            // Mettre à jour teamSubmission si fourni
            if (evaluationDetails.getTeamSubmission() != null && evaluationDetails.getTeamSubmission().getId() != null) {
                TeamSubmission teamSubmission = teamSubmissionRepository.findById(evaluationDetails.getTeamSubmission().getId())
                        .orElseThrow(() -> new IllegalArgumentException("TeamSubmission avec ID " + evaluationDetails.getTeamSubmission().getId() + " non trouvé."));
                existingEvaluation.setTeamSubmission(teamSubmission);
            }

            // Mettre à jour evaluator si fourni
            if (evaluationDetails.getEvaluator() != null && evaluationDetails.getEvaluator().getId() != null) {
                User evaluator = userRepository.findById(evaluationDetails.getEvaluator().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec ID " + evaluationDetails.getEvaluator().getId() + " non trouvé."));
                existingEvaluation.setEvaluator(evaluator);
            }

            return projectEvaluationRepository.save(existingEvaluation);
        } else {
            throw new EntityNotFoundException("Évaluation avec ID " + id + " non trouvée.");
        }
    }

    // DELETE
    public void deleteProjectEvaluation(Long id) {
        Optional<ProjectEvaluation> evaluationOpt = projectEvaluationRepository.findById(id);
        if (evaluationOpt.isPresent()) {
            projectEvaluationRepository.delete(evaluationOpt.get());
        } else {
            throw new EntityNotFoundException("Évaluation avec ID " + id + " non trouvée.");
        }
    }

    // Nouvelle méthode pour les projets les mieux notés
    public List<ProjectEvaluation> getTopRatedProjects(Integer minScore) {
        return projectEvaluationRepository.findByScoreGreaterThanEqual(minScore);
    }
}