package com.esprit.pi.repositories;

import com.esprit.pi.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQuestionRepository extends JpaRepository<Question, Long> {
    // List<Question> findByQuizId(Long quizId);

}

