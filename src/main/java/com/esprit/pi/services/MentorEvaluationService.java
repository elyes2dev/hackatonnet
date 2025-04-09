package com.esprit.pi.services;

import com.esprit.pi.entities.MentorEvaluation;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.MentorEvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MentorEvaluationService {

    @Autowired
    private MentorEvaluationRepository mentorEvaluationRepository;

    @Autowired
    private UserService userService;
    // Create
    @Transactional
    public MentorEvaluation createEvaluation(MentorEvaluation evaluation) {
        // Set creation date if not already set
        if (evaluation.getCreatedAt() == null) {
            evaluation.setCreatedAt(new Date());
        }

        // Validate rating
        if (evaluation.getRating() < 0 || evaluation.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        MentorEvaluation savedEvaluation = mentorEvaluationRepository.save(evaluation);

        // Update mentor's points and badge
        if (savedEvaluation.getMentor() != null) {
            userService.updateUserPointsAndBadge(savedEvaluation.getMentor().getId());
        }


        return savedEvaluation;
    }

    // Update existing methods to handle points recalculation
    @Transactional
    public MentorEvaluation updateEvaluation(MentorEvaluation evaluation) {
        // Verify the evaluation exists
        if (evaluation.getId() == null || !mentorEvaluationRepository.existsById(evaluation.getId())) {
            throw new IllegalArgumentException("Evaluation with ID " + evaluation.getId() + " not found");
        }

        // Validate rating
        if (evaluation.getRating() < 0 || evaluation.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        MentorEvaluation savedEvaluation = mentorEvaluationRepository.save(evaluation);

        // Update mentor's points
        if (savedEvaluation.getMentor() != null) {
            userService.updateUserPointsAndBadge(savedEvaluation.getMentor().getId());
        }

        return savedEvaluation;
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


    // Delete
    public void deleteEvaluation(Long id) {
        mentorEvaluationRepository.deleteById(id);
    }
}