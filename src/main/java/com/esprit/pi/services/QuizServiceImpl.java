package com.esprit.pi.services;

import com.esprit.pi.entities.Quiz;
import com.esprit.pi.repositories.IQuestionRepository;
import com.esprit.pi.repositories.IQuizRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizServiceImpl implements IQuizService {
    @Autowired
    private IQuizRepository quizRepository;

    @Autowired
    private IQuestionRepository questionRepository;

    @Override
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuiz(Long id, Quiz quiz) {
        if (quizRepository.existsById(id)) {
            quiz.setId_quiz(id);
            return quizRepository.save(quiz);
        }
        throw new RuntimeException("Quiz not found");
    }

    @Override
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + id));

        // ✅ Explicitly delete all questions first
        if (!quiz.getQuestions().isEmpty()) {
            questionRepository.deleteAll(quiz.getQuestions());
        }

        // ✅ Now delete the quiz
        quizRepository.delete(quiz);
    }


}

