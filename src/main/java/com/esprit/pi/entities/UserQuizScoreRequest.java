package com.esprit.pi.entities;

import java.util.List;

public class UserQuizScoreRequest {
    private Long userId;
    private Long quizId;
    private Integer score;

    // Constructors, getters, and setters
    public UserQuizScoreRequest() {}

    public UserQuizScoreRequest(Long userId, Long quizId, Integer score) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

// Standard getters and setters
}