package com.esprit.pi.services;

import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.entities.TeamSubmission;
import com.esprit.pi.repositories.TeamMembersRepository;
import com.esprit.pi.repositories.TeamSubmissionRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


import java.util.List;
import java.util.Optional;

@Service
public class TeamSubmissionService {

    @Autowired
    private TeamSubmissionRepository teamSubmissionRepository;

    @Autowired
    private TeamMembersRepository teamMembersRepository;

    public TeamSubmission createTeamSubmission(TeamSubmission teamSubmission) {
        if (teamSubmission.getTeamMember() == null || teamSubmission.getTeamMember().getId() == null) {
            throw new IllegalArgumentException("Le membre d'équipe est obligatoire et doit avoir un ID valide.");
        }

        Long teamMemberId = teamSubmission.getTeamMember().getId();
        // Vérifier si le TeamMembers existe
        TeamMembers teamMember = teamMembersRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Membre d'équipe avec ID " + teamMemberId + " non trouvé."));

        // Vérifier si une soumission existe déjà pour ce membre
        List<TeamSubmission> existingSubmissions = teamSubmissionRepository.findByTeamMemberId(teamMemberId);
        if (!existingSubmissions.isEmpty()) {
            throw new IllegalArgumentException("Ce membre d'équipe a déjà soumis un projet !");
        }

        // Assigner le TeamMembers valide à la soumission
        teamSubmission.setTeamMember(teamMember);

        // Définir la date de soumission si non fournie
        if (teamSubmission.getSubmissionDate() == null) {
            teamSubmission.setSubmissionDate(LocalDateTime.now());
        }

        // Enregistrer la soumission
        return teamSubmissionRepository.save(teamSubmission);
    }
    // Read
    public List<TeamSubmission> getAllTeamSubmissions() {
        return teamSubmissionRepository.findAll();
    }

    public Optional<TeamSubmission> getTeamSubmissionById(Long id) {
        return teamSubmissionRepository.findById(id);
    }

    public List<TeamSubmission> getTeamSubmissionByTeamMemberId(Long teamMemberId) {
        return teamSubmissionRepository.findByTeamMemberId(teamMemberId); // Retourne une liste
    }

    public List<TeamSubmission> searchTeamSubmissionsByProjectName(String keyword) {
        return teamSubmissionRepository.findByProjectNameContaining(keyword);
    }

    // Update
    public TeamSubmission updateTeamSubmission(Long id, TeamSubmission teamSubmissionDetails) {
        Optional<TeamSubmission> optionalTeamSubmission = teamSubmissionRepository.findById(id);
        if (optionalTeamSubmission.isPresent()) {
            TeamSubmission existingTeamSubmission = optionalTeamSubmission.get();

            existingTeamSubmission.setProjectName(teamSubmissionDetails.getProjectName());
            existingTeamSubmission.setDescription(teamSubmissionDetails.getDescription());
            existingTeamSubmission.setRepoLink(teamSubmissionDetails.getRepoLink());

            // Mettre à jour seulement si le teamMember est fourni
            if (teamSubmissionDetails.getTeamMember() != null) {
                existingTeamSubmission.setTeamMember(teamSubmissionDetails.getTeamMember());
            }

            return teamSubmissionRepository.save(existingTeamSubmission);
        } else {
            throw new RuntimeException("Team submission not found with id: " + id);
        }
    }

    // Delete

    public void deleteTeamSubmission(Long id) {
        Optional<TeamSubmission> teamSubmissionOpt = teamSubmissionRepository.findById(id);
        if (teamSubmissionOpt.isPresent()) {
            TeamSubmission teamSubmission = teamSubmissionOpt.get();

            // Vérifie s'il y a des évaluations liées et supprime-les si nécessaire
            // Exemple : projectEvaluationRepository.deleteByTeamSubmissionId(id);

            // Détache la relation avec TeamMembers
            if (teamSubmission.getTeamMember() != null) {
                TeamMembers member = teamSubmission.getTeamMember();
                member.setTeamSubmission(null);  // Retire la référence de TeamSubmission
                teamMembersRepository.save(member);  // Sauvegarde le membre de l'équipe sans la référence
            }

            // Supprime la TeamSubmission
            teamSubmissionRepository.delete(teamSubmission);
            teamSubmissionRepository.flush();  // Assure-toi que la suppression est bien exécutée

        } else {
            throw new EntityNotFoundException("TeamSubmission not found with id: " + id);
        }
    }
}
