package com.esprit.pi.repositories;

import com.esprit.pi.entities.UserQuizScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserQuizScoreRepository extends JpaRepository<UserQuizScore, Long> {
    // Existing method
    @Query("SELECT uqs FROM UserQuizScore uqs WHERE uqs.user.id = :userId AND uqs.quiz.id = :quizId")
    Optional<UserQuizScore> findByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // New methods with explicit JPQL queries
    @Query("SELECT uqs FROM UserQuizScore uqs WHERE uqs.user.id = :userId")
    List<UserQuizScore> findByUserId(@Param("userId") Long userId);

    @Query("SELECT uqs FROM UserQuizScore uqs WHERE uqs.quiz.id = :quizId")
    List<UserQuizScore> findByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT CASE WHEN COUNT(uqs) > 0 THEN true ELSE false END FROM UserQuizScore uqs WHERE uqs.user.id = :userId AND uqs.quiz.id = :quizId")
    boolean existsByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Modifying
    @Query("DELETE FROM UserQuizScore uqs WHERE uqs.user.id = :userId AND uqs.quiz.id = :quizId")
    void deleteByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);
}
