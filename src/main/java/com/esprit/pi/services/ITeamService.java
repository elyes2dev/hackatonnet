package com.esprit.pi.services;

import com.esprit.pi.entities.Team;

import java.util.List;

public interface ITeamService {
    List<Team> displayTeams();
    Team addTeam(Team team);
    void deleteTeam(long id);
    Team updateTeam(Team team);
}