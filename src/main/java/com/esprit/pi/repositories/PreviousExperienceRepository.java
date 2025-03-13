package com.esprit.pi.repositories;

import com.esprit.pi.entities.PreviousExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreviousExperienceRepository extends JpaRepository<PreviousExperience, Long> {
    List<PreviousExperience> findByApplicationId(Long applicationId);
    List<PreviousExperience> findByYear(int year);
    List<PreviousExperience> findByHackathonNameContaining(String keyword);
}