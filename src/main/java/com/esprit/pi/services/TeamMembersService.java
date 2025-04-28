package com.esprit.pi.services;

import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.repositories.TeamMembersRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class TeamMembersService implements ITeamMembersService  {

    @Autowired
    private TeamMembersRepository teamMembersRepository;


    public List<TeamMembers> displayTeamMembers() {
        return teamMembersRepository.findAll();
    }

    public TeamMembers addTeamMember(TeamMembers teamMembers) {
        return teamMembersRepository.save(teamMembers);
    }

    public void deleteTeamMember(Long id) {
        teamMembersRepository.deleteById(id);
    }

    public TeamMembers updateTeamMember(TeamMembers teamMember) {
        if (teamMember.getId() == null) {
            throw new RuntimeException("TeamMember ID cannot be null for update operation");
        }
        return teamMembersRepository.save(teamMember);
    }

    public TeamMembers findTeamMemberById(Long id) {
        return teamMembersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeamMember not found with ID: " + id));
    }

    public List<TeamMembers> findMembersByTeamId(Long teamId) {
        return teamMembersRepository.findByTeamId(teamId);
    }

    public List<TeamMembers> findAllTeamMembersByUserId(Long userId) {
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByUserId(userId);
        if (teamMembers.isEmpty()) {
            throw new RuntimeException("No team memberships found for user ID: " + userId);
        }
        return teamMembers;
    }

    // Remove @Override for troubleshooting
    public TeamMembers findTeamMemberByUserId(Long userId) {
        List<TeamMembers> teamMembers = teamMembersRepository.findAllByUserId(userId);
        if (teamMembers.isEmpty()) {
            throw new RuntimeException("TeamMember not found for user ID: " + userId);
        }
        return teamMembers.get(0);
    }
}