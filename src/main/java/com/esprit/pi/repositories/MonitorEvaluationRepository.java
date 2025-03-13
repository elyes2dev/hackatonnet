package com.esprit.pi.repositories;

import com.esprit.pi.entities.MonitorEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitorEvaluationRepository extends JpaRepository<MonitorEvaluation, Long> {
    List<MonitorEvaluation> findByMentorId(Long mentorId);
    List<MonitorEvaluation> findByTeamId(Long teamId);
    List<MonitorEvaluation> findByRatingGreaterThanEqual(int rating);
}