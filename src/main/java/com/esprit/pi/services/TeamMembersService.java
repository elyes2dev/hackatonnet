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
public class TeamMembersService implements ITeamMembersService {

    @Autowired
    private TeamMembersRepository teamMembersRepository;

    @Override
    public List<TeamMembers> displayTeamMembers() {
        return teamMembersRepository.findAll();
    }

    @Override
    public TeamMembers addTeamMember(TeamMembers teamMembers) {
        return teamMembersRepository.save(teamMembers);
    }

    @Override
    public void deleteTeamMember(long id) {
        teamMembersRepository.deleteById(id);
    }

    @Override
    public TeamMembers updateTeamMember(TeamMembers teamMember) {
        return teamMembersRepository.save(teamMember);
    }

    @Override
    public TeamMembers findTeamMemberById(long id) {
        return teamMembersRepository.findById(id).orElseThrow(() -> new RuntimeException("TeamMember not found"));
    }
}