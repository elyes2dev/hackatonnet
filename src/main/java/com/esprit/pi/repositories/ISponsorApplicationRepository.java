package com.esprit.pi.repositories;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.SponsorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISponsorApplicationRepository extends JpaRepository<SponsorApplication,Integer> {
    Optional<SponsorApplication> findByUserId(long userId);
    List<SponsorApplication> findByStatusAndSubmittedAtBefore(ApplicationStatus status, LocalDateTime dateTime);
    List<SponsorApplication> findByStatusAndSubmittedAtBeforeAndNotifiedFalse(ApplicationStatus status, LocalDateTime time);


}
