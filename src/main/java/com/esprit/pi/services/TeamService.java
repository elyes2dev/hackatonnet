package com.esprit.pi.services;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.Team;
import com.esprit.pi.repositories.TeamRepository;
import com.esprit.pi.repositories.HackathonRepository;
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
public class TeamService implements ITeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private HackathonRepository hackathonRepository;

    @Override
    public List<Team> displayTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Team addTeam(Team team) {
        Hackathon hackathon = hackathonRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));
        team.setHackathon(hackathon);
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }
}