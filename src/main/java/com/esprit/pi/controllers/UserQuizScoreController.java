package com.esprit.pi.controllers;

import com.esprit.pi.entities.UserQuizScore;
import com.esprit.pi.entities.UserQuizScoreRequest;
import com.esprit.pi.services.UserQuizScoreServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/score")
public class UserQuizScoreController {
    private final UserQuizScoreServiceImpl quizScoreService;

    public UserQuizScoreController(UserQuizScoreServiceImpl quizScoreService) {
        this.quizScoreService = quizScoreService;
    }

    // Existing endpoint
    @GetMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<Integer> getUserQuizScore(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        Integer score = quizScoreService.getUserScoreForQuiz(userId, quizId);
        return ResponseEntity.ok(score);
    }

    // Get all scores for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserQuizScore>> getUserScores(@PathVariable Long userId) {
        List<UserQuizScore> scores = quizScoreService.getUserScores(userId);
        return ResponseEntity.ok(scores);
    }

    // Get all scores for a quiz
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<UserQuizScore>> getQuizScores(@PathVariable Long quizId) {
        List<UserQuizScore> scores = quizScoreService.getQuizScores(quizId);
        return ResponseEntity.ok(scores);
    }

    // Create or update a score
    // Updated saveScore endpoint using RequestBody
    @PostMapping
    public ResponseEntity<UserQuizScore> saveScore(@RequestBody UserQuizScoreRequest request) {
        UserQuizScore savedScore = quizScoreService.saveOrUpdateScore(request);
        return ResponseEntity.ok(savedScore);
    }

    // Delete a score
    @DeleteMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<Void> deleteScore(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        quizScoreService.deleteUserQuizScore(userId, quizId);
        return ResponseEntity.noContent().build();
    }

    // Check if user has taken a quiz
    @GetMapping("/user/{userId}/quiz/{quizId}/exists")
    public ResponseEntity<Boolean> hasUserTakenQuiz(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        boolean exists = quizScoreService.hasUserTakenQuiz(userId, quizId);
        return ResponseEntity.ok(exists);
    }
}