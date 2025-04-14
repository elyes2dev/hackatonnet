package com.esprit.pi.repositories;

import com.esprit.pi.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByTeamCode(String teamCode);
    List<Team> findByHackathonId(Long hackathonId);
    List<Team> findByIsPublicTrueAndIsFullFalse();// For public teams that aren't full
    Optional<Team> findById(Long id);
}