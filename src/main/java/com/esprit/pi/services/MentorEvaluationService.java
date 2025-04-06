package com.esprit.pi.services;

import com.esprit.pi.entities.MentorEvaluation;
import com.esprit.pi.repositories.MentorEvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MentorEvaluationService {

    @Autowired
    private MentorEvaluationRepository mentorEvaluationRepository;

    // Create
    public MentorEvaluation createEvaluation(MentorEvaluation evaluation) {
        // Set creation date if not already set
        if (evaluation.getCreatedAt() == null) {
            evaluation.setCreatedAt(new Date());
        }

        // Validate rating
        if (evaluation.getRating() < 1 || evaluation.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        return mentorEvaluationRepository.save(evaluation);
    }

    // Read
    public List<MentorEvaluation> getAllEvaluations() {
        return mentorEvaluationRepository.findAll();
    }

    public Optional<MentorEvaluation> getEvaluationById(Long id) {
        return mentorEvaluationRepository.findById(id);
    }

    // Updated to be clearer - these are evaluations where the user is the mentor
    public List<MentorEvaluation> getEvaluationsByMentorId(Long mentorId) {
        return mentorEvaluationRepository.findByMentorId(mentorId);
    }

    public List<MentorEvaluation> getEvaluationsByTeamId(Long teamId) {
        return mentorEvaluationRepository.findByTeamId(teamId);
    }

    public List<MentorEvaluation> getEvaluationsByMinimumRating(int minRating) {
        return mentorEvaluationRepository.findByRatingGreaterThanEqual(minRating);
    }
    // Update
    public MentorEvaluation updateEvaluation(MentorEvaluation evaluation) {
        // Validate rating
        if (evaluation.getRating() < 1 || evaluation.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        return mentorEvaluationRepository.save(evaluation);
    }

    // Delete
    public void deleteEvaluation(Long id) {
        mentorEvaluationRepository.deleteById(id);
    }
}