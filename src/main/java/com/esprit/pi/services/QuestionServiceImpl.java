package com.esprit.pi.services;

import com.esprit.pi.entities.Question;
import com.esprit.pi.repositories.IQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private IQuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long id, Question question) {
        if (questionRepository.existsById(id)) {
            question.setId_question(id);
            return questionRepository.save(question);
        }
        throw new RuntimeException("Question not found");
    }

    @Override
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

   // @Override
  //  public List<Question> getQuestionsByQuiz(Long quizId) {
   //     return questionRepository.findByQuizId(quizId);
   // }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
    // Method to get questions by quizId
    @Override
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findQuestionsByQuizId(quizId);
    }


}

