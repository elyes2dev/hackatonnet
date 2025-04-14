package com.esprit.pi.services;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.Team;
import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.repositories.TeamRepository;
import com.esprit.pi.repositories.HackathonRepository;
import com.esprit.pi.repositories.TeamMembersRepository;
import com.esprit.pi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class TeamService implements ITeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private TeamMembersRepository teamMembersRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int JOIN_CODE_EXPIRATION_HOURS = 24; // Increased to 24 hours for practicality

    @Override
    public List<Team> displayTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Team addTeam(Team team) {
        team.setTeamCode(generateUniqueTeamCode());
        team.setJoinCodeExpirationTime(calculateExpirationTime());
        team.setIsFull(false);
        return teamRepository.save(team);
    }

    @Override
    public Team findTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + id));
    }

    private String generateUniqueTeamCode() {
        Random random = new Random();
        StringBuilder code;
        do {
            code = new StringBuilder("TEAM-");
            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
        } while (teamRepository.findByTeamCode(code.toString()) != null);
        return code.toString();
    }

    private Date calculateExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, JOIN_CODE_EXPIRATION_HOURS);
        return calendar.getTime();
    }

    @Override
    public Team createTeamWithLeader(Team team, Long hackathonId, Long leaderId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        if (teamMembersRepository.findByUserIdAndTeamHackathonId(leaderId, hackathonId).isPresent()) {
            throw new RuntimeException("User is already in a team for this hackathon");
        }

        team.setTeamCode(generateUniqueTeamCode());
        team.setJoinCodeExpirationTime(calculateExpirationTime());
        team.setHackathon(hackathon);
        team.setIsFull(false);
        Team savedTeam = teamRepository.save(team);

        TeamMembers leader = new TeamMembers();
        leader.setTeam(savedTeam);
        leader.setUser(userRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        leader.setRole(TeamMembers.Role.LEADER);
        teamMembersRepository.save(leader);

        return savedTeam;
    }

    @Override
    public Team joinTeamByCode(String teamCode, Long userId) {
        Team team = teamRepository.findByTeamCode(teamCode);
        if (team == null) {
            throw new RuntimeException("Invalid team code");
        }

        if (team.getJoinCodeExpirationTime() == null || new Date().after(team.getJoinCodeExpirationTime())) {
            throw new RuntimeException("Team join code has expired");
        }

        if (team.getIsFull()) {
            throw new RuntimeException("Team is full");
        }

        Hackathon hackathon = team.getHackathon();
        if (teamMembersRepository.findByUserIdAndTeamHackathonId(userId, hackathon.getId()).isPresent()) {
            throw new RuntimeException("User is already in a team for this hackathon");
        }

        TeamMembers member = new TeamMembers();
        member.setTeam(team);
        member.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        member.setRole(TeamMembers.Role.MEMBER);
        teamMembersRepository.save(member);

        long memberCount = teamMembersRepository.countByTeamId(team.getId());
        Integer maxTeamSize = hackathon.getMaxTeamSize();
        if (maxTeamSize != null && memberCount >= maxTeamSize) {
            team.setIsFull(true);
            teamRepository.save(team);
        }

        return team;
    }

    @Override
    public void leaveTeam(Long userId, Long teamId) {
        TeamMembers teamMember = teamMembersRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new RuntimeException("User is not in this team"));
        Team team = teamMember.getTeam();

        if (teamMember.getRole() == TeamMembers.Role.LEADER) {
            Optional<TeamMembers> newLeaderOpt = teamMembersRepository.findFirstByTeamAndRoleNot(team, TeamMembers.Role.LEADER);
            newLeaderOpt.ifPresent(newLeader -> {
                newLeader.setRole(TeamMembers.Role.LEADER);
                teamMembersRepository.save(newLeader);
            });
            if (newLeaderOpt.isEmpty() && teamMembersRepository.countByTeamId(team.getId()) > 1) {
                throw new RuntimeException("Cannot leave team without assigning a new leader");
            }
        }

        teamMembersRepository.delete(teamMember);

        if (team.getIsFull()) {
            team.setIsFull(false);
            teamRepository.save(team);
        }
    }

    @Override
    public String regenerateTeamCode(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        String newCode = generateUniqueTeamCode();
        team.setTeamCode(newCode);
        team.setJoinCodeExpirationTime(calculateExpirationTime());
        teamRepository.save(team);
        return newCode;
    }

    @Override
    public boolean isJoinCodeValid(String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode);
        return team != null && team.getJoinCodeExpirationTime() != null &&
                new Date().before(team.getJoinCodeExpirationTime()) && !team.getIsFull();
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public Team updateTeam(Team team) {
        if (team.getId() == null) {
            throw new RuntimeException("Team ID cannot be null for update operation");
        }
        return teamRepository.save(team);
    }

    @Override
    public List<Team> getAvailablePublicTeams() {
        return teamRepository.findByIsPublicTrueAndIsFullFalse();
    }
}