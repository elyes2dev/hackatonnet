package com.esprit.pi.repositories;

import com.esprit.pi.entities.Question;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IQuestionRepository extends JpaRepository<Question, Long> {
    // List<Question> findByQuizId(Long quizId);
  //  @Modifying
 //   @Transactional
 //   @Query("DELETE FROM QuestionAnswer qa WHERE qa.question.id_question IN (SELECT q.id_question FROM Question q WHERE q.quiz.id_quiz = :quizId)")
 //   void deleteAnswersByQuizId(@Param("quizId") Long quizId);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id_quiz = :quizId")
    void deleteQuestionsByQuizId(Long quizId);

    @Modifying
    @Query(value = "DELETE FROM question_answers WHERE question_id IN (SELECT id_question FROM question WHERE quiz_id = :quizId)", nativeQuery = true)
    void deleteAnswersByQuizId(Long quizId);



}

