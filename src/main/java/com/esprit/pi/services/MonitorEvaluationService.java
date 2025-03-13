package com.esprit.pi.services;

import com.esprit.pi.entities.MonitorEvaluation;
import com.esprit.pi.repositories.MonitorEvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MonitorEvaluationService {

    @Autowired
    private MonitorEvaluationRepository monitorEvaluationRepository;

    // Create
    public MonitorEvaluation createEvaluation(MonitorEvaluation evaluation) {
        // Set creation date if not already set
        if (evaluation.getCreatedAt() == null) {
            evaluation.setCreatedAt(new Date());
        }

        // Validate rating
        if (evaluation.getRating() < 1 || evaluation.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        return monitorEvaluationRepository.save(evaluation);
    }

    // Read
    public List<MonitorEvaluation> getAllEvaluations() {
        return monitorEvaluationRepository.findAll();
    }

    public Optional<MonitorEvaluation> getEvaluationById(Long id) {
        return monitorEvaluationRepository.findById(id);
    }

    public List<MonitorEvaluation> getEvaluationsByMentorId(Long mentorId) {
        return monitorEvaluationRepository.findByMentorId(mentorId);
    }

    public List<MonitorEvaluation> getEvaluationsByTeamId(Long teamId) {
        return monitorEvaluationRepository.findByTeamId(teamId);
    }

    public List<MonitorEvaluation> getEvaluationsByMinimumRating(int minRating) {
        return monitorEvaluationRepository.findByRatingGreaterThanEqual(minRating);
    }

    // Update
    public MonitorEvaluation updateEvaluation(MonitorEvaluation evaluation) {
        // Validate rating
        if (evaluation.getRating() < 1 || evaluation.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        return monitorEvaluationRepository.save(evaluation);
    }

    // Delete
    public void deleteEvaluation(Long id) {
        monitorEvaluationRepository.deleteById(id);
    }
}