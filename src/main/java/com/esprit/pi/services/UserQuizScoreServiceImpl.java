package com.esprit.pi.services;

import com.esprit.pi.entities.*;
import com.esprit.pi.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserQuizScoreServiceImpl  {

    private final IUserQuizScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final IQuizRepository quizRepository;

    @Autowired
    public UserQuizScoreServiceImpl(IUserQuizScoreRepository scoreRepository,
                                    UserRepository userRepository,
                                    IQuizRepository quizRepository) {
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
    }

    public Integer getUserScoreForQuiz(Long userId, Long quizId) {
        return scoreRepository.findByUserIdAndQuizId(userId, quizId)
                .map(UserQuizScore::getScore)
                .orElse(null);
    }

    public List<UserQuizScore> getUserScores(Long userId) {
        return scoreRepository.findByUserId(userId);
    }

    public List<UserQuizScore> getQuizScores(Long quizId) {
        return scoreRepository.findByQuizId(quizId);
    }

    public UserQuizScore saveOrUpdateScore(UserQuizScoreRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        UserQuizScore score = scoreRepository.findByUserIdAndQuizId(request.getUserId(), request.getQuizId())
                .orElse(new UserQuizScore());

        score.setUser(user);
        score.setQuiz(quiz);
        score.setScore(request.getScore());

        return scoreRepository.save(score);
    }

    public void deleteUserQuizScore(Long userId, Long quizId) {
        if (!scoreRepository.existsByUserIdAndQuizId(userId, quizId)) {
            throw new RuntimeException("Score not found");
        }
        scoreRepository.deleteByUserIdAndQuizId(userId, quizId);
    }

    public boolean hasUserTakenQuiz(Long userId, Long quizId) {
        return scoreRepository.existsByUserIdAndQuizId(userId, quizId);
    }
}


