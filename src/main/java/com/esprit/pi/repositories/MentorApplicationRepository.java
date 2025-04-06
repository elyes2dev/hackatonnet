package com.esprit.pi.repositories;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.MentorApplication;
import com.esprit.pi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorApplicationRepository extends JpaRepository<MentorApplication, Long> {

        List<MentorApplication> findByUserId(Long userId);
        List<MentorApplication> findByStatus(ApplicationStatus status);
        List<MentorApplication> findByHasPreviousExperience(boolean hasPreviousExperience);
    }
