package com.esprit.pi.services;

import com.esprit.pi.entities.Question;
import com.esprit.pi.entities.Quiz;
import com.esprit.pi.repositories.IQuestionRepository;
import com.esprit.pi.repositories.IQuizRepository;
import com.esprit.pi.repositories.IUserQuizScoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizServiceImpl implements IQuizService {
    @Autowired
    private IQuizRepository quizRepository;

    @Autowired
    private IUserQuizScoreRepository userQuizScoreRepository;

    @Autowired
    private IQuestionRepository questionRepository;

    @Override
    public Quiz createQuiz(Quiz quiz) {
        // If there are questions, link them to the quiz
        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            for (Question question : quiz.getQuestions()) {
                question.setQuiz(quiz); // Set the quiz for each question


                if (question.getAnswers() == null) {
                    question.setAnswers(new ArrayList<>());
                }
            }
        }

        // Save the quiz (which will cascade and save questions as well)
        return quizRepository.save(quiz);
    }


    @Override
    public Quiz updateQuiz(Long id, Quiz quiz) {
        if (quizRepository.existsById(id)) {
            // Set the Quiz ID to ensure proper updating
            quiz.setId_quiz(id);

            // Update the questions: Set the quiz reference for each question
            for (Question question : quiz.getQuestions()) {
                question.setQuiz(quiz);  // Ensure the question is associated with the updated quiz
            }

            // Save the quiz and its associated questions
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

        // Delete all UserQuizScore records related to the quiz
        userQuizScoreRepository.deleteByQuizId(id);
        // First, delete all answers associated with the quiz
        questionRepository.deleteAnswersByQuizId(id);

        // Now delete all questions related to the quiz
        questionRepository.deleteQuestionsByQuizId(id);

        // Delete the quiz directly using a query
        quizRepository.deleteQuizById(id);
    }


    @Override
    public List<Quiz> getQuizzesByWorkshop(Long workshopId) {
        return quizRepository.findByWorkshopId(workshopId);
    }





}

