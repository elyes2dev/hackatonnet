package com.esprit.pi.repositories;

import com.esprit.pi.entities.TeamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamSubmissionRepository extends JpaRepository<TeamSubmission, Long> {
    // Retourner une liste de soumissions pour un membre d'équipe donné
    List<TeamSubmission> findByTeamMemberId(Long teamMemberId);

    // Recherche par nom de projet
    List<TeamSubmission> findByProjectNameContaining(String keyword);

    // Retourner une liste de soumissions par statut

}
