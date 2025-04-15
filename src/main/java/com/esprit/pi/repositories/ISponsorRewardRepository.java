package com.esprit.pi.repositories;

import com.esprit.pi.entities.SponsorReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISponsorRewardRepository extends JpaRepository<SponsorReward,Long> {
    Optional<SponsorReward> findBySponsorId(Long sponsorId);
}
