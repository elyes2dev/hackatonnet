package com.esprit.pi.repositories;

import com.esprit.pi.entities.MentorEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorEvaluationRepository extends JpaRepository<MentorEvaluation, Long> {
    List<MentorEvaluation> findByMentorId(Long mentorId);
    List<MentorEvaluation> findByTeamId(Long teamId);
    List<MentorEvaluation> findByRatingGreaterThanEqual(int minRating);
}