package com.esprit.pi.Repository;

import com.esprit.pi.entities.SponsorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISponsorApplicationRepository extends JpaRepository<SponsorApplication,Integer> {
    Optional<SponsorApplication> findByUserId(long userId);
}
