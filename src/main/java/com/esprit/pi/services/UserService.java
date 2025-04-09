package com.esprit.pi.services;

import com.esprit.pi.entities.MentorEvaluation;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.MentorEvaluationRepository;
import com.esprit.pi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorEvaluationRepository mentorEvaluationRepository;
    @Transactional
    public User updateUserPointsAndBadge(Long userId) {
        // Get a fresh entity from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Get all evaluations for this user directly from the repository
        List<MentorEvaluation> evaluations = mentorEvaluationRepository.findByMentorId(userId);

        // Calculate points based on these evaluations - don't set the evaluations property
        int totalPoints = evaluations.stream()
                .mapToInt(eval -> {
                    int basePoints = 10;
                    int ratingBonus = eval.getRating() * 2;
                    return basePoints + ratingBonus;
                })
                .sum();

        // Set the mentor points and update badge
        user.setMentorPoints(totalPoints);

        // Update badge based on points
        if (totalPoints >= 20) {
            user.setBadge(User.BadgeLevel.MASTER_MENTOR);
        } else if (totalPoints >= 15) {
            user.setBadge(User.BadgeLevel.HEAD_COACH);
        } else if (totalPoints >= 10) {
            user.setBadge(User.BadgeLevel.SENIOR_COACH);
        } else if (totalPoints >= 5) {
            user.setBadge(User.BadgeLevel.ASSISTANT_COACH);
        } else {
            user.setBadge(User.BadgeLevel.JUNIOR_COACH);
        }

        // Save and return
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}