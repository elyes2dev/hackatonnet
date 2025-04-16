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
public class TeamMembersService  {

    @Autowired
    private TeamMembersRepository teamMembersRepository;


    public List<TeamMembers> displayTeamMembers() {
        return teamMembersRepository.findAll();
    }

    public TeamMembers addTeamMember(TeamMembers teamMembers) {
        return teamMembersRepository.save(teamMembers);
    }

    public void deleteTeamMember(long id) {
        teamMembersRepository.deleteById(id);
    }

    public TeamMembers updateTeamMember(TeamMembers teamMember) {
        return teamMembersRepository.save(teamMember);
    }

    public TeamMembers findTeamMemberById(long id) {
        return teamMembersRepository.findById(id).orElseThrow(() -> new RuntimeException("TeamMember not found"));
    }
}