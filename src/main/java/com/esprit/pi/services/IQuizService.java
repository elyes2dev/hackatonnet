package com.esprit.pi.services;

import com.esprit.pi.entities.Quiz;

import java.util.List;
import java.util.Optional;

public interface IQuizService {
    Quiz createQuiz(Quiz quiz);
    Quiz updateQuiz(Long id, Quiz quiz);
    Optional<Quiz> getQuizById(Long id);
    List<Quiz> getAllQuizzes();
    void deleteQuiz(Long id);
}

