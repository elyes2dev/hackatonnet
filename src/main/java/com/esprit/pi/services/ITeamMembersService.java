package com.esprit.pi.services;

import com.esprit.pi.entities.TeamMembers;

import java.util.List;

public interface ITeamMembersService {
    List<TeamMembers> displayTeamMembers();
    TeamMembers addTeamMember(TeamMembers teamMembers);
    void deleteTeamMember(Long id);
    TeamMembers updateTeamMember(TeamMembers teamMember);
    TeamMembers findTeamMemberById(Long id);
    List<TeamMembers> findMembersByTeamId(Long teamId);
}