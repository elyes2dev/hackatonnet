package com.esprit.pi.repositories;

import com.esprit.pi.entities.ProjectEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectEvaluationRepository extends JpaRepository<ProjectEvaluation, Long> {
    List<ProjectEvaluation> findByTeamSubmissionId(Long teamSubmissionId);
    List<ProjectEvaluation> findByEvaluatorId(Long evaluatorId);
    List<ProjectEvaluation> findByScoreGreaterThanEqual(int minScore);

}