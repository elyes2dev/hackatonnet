package com.esprit.pi.services;

import com.esprit.pi.entities.Team;

import java.util.List;

public interface ITeamService {
    List<Team> displayTeams();
    Team addTeam(Team team);
    Team findTeamById(Long id);
    Team createTeamWithLeader(Team team, Long hackathonId, Long leaderId);
    Team joinTeamByCode(String teamCode, Long userId);
    void leaveTeam(Long userId, Long teamId);
    String regenerateTeamCode(Long teamId);
    boolean isJoinCodeValid(String teamCode);
    void deleteTeam(Long id);
    Team updateTeam(Team team);
    List<Team> getAvailablePublicTeams();
}