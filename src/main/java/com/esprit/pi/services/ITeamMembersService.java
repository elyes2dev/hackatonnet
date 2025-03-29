package com.esprit.pi.services;

import com.esprit.pi.entities.TeamMembers;

import java.util.List;

public interface ITeamMembersService {
    List<TeamMembers> displayTeamMembers();
    TeamMembers addTeamMember(TeamMembers teamMembers);
    void deleteTeamMember(long id);
    TeamMembers updateTeamMember(TeamMembers teamMembers);
    TeamMembers findTeamMemberById(long id);
}