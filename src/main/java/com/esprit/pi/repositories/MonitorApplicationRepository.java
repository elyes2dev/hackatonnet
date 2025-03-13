package com.esprit.pi.repositories;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MonitorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitorApplicationRepository extends JpaRepository<MonitorApplication, Long> {
    List<MonitorApplication> findByMentorId(Long mentorId);
    List<MonitorApplication> findByStatus(ApplicationStatus status);
    List<MonitorApplication> findByHasPreviousExperience(boolean hasPreviousExperience);
}