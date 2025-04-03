package com.esprit.pi.services;

import com.esprit.pi.entities.Question;

import java.util.List;
import java.util.Optional;

public interface IQuestionService {
    Question createQuestion(Question question);
    Question updateQuestion(Long id, Question question);
    Optional<Question> getQuestionById(Long id);
  //  List<Question> getQuestionsByQuiz(Long quizId);
    void deleteQuestion(Long id);
    List<Question> getAllQuestions();
}
