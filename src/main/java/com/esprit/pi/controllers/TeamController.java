package com.esprit.pi.controllers;

import com.esprit.pi.dtos.ChatMessageDTO;
import com.esprit.pi.entities.Team;
import com.esprit.pi.entities.TeamDiscussion;
import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.services.ITeamDiscussionService;
import com.esprit.pi.services.ITeamMembersService;
import com.esprit.pi.services.ITeamService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TeamController {

    private final ITeamService teamService;
    private final ITeamMembersService teamMembersService;
    private final ITeamDiscussionService teamDiscussionService;
    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    @Autowired
    public TeamController(ITeamService teamService,
                          ITeamMembersService teamMembersService,
                          ITeamDiscussionService teamDiscussionService) {
        this.teamService = teamService;
        this.teamMembersService = teamMembersService;
        this.teamDiscussionService = teamDiscussionService;
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.displayTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        try {
            Team team = teamService.findTeamById(id);
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/create/{hackathonId}/{leaderId}")
    public ResponseEntity<?> createTeam(
            @RequestBody Team team,
            @PathVariable Long hackathonId,
            @PathVariable Long leaderId) {
        try {
            Team createdTeam = teamService.createTeamWithLeader(team, hackathonId, leaderId);
            TeamMembers leaderMember = teamMembersService.findMembersByTeamId(createdTeam.getId())
                    .stream()
                    .filter(tm -> tm.getRole() == TeamMembers.Role.LEADER)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Leader not found after team creation"));

            TeamDiscussion discussion = teamDiscussionService.createDiscussion(
                    leaderMember.getId(),
                    "Team " + createdTeam.getTeamName() + " created!",
                    ChatMessageDTO.TeamDiscussionType.TEXT
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/join/{teamCode}/{userId}")
    public ResponseEntity<?> joinTeam(
            @PathVariable String teamCode,
            @PathVariable Long userId) {
        try {
            if (!teamService.isJoinCodeValid(teamCode)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid or expired team code"));
            }
            Team team = teamService.joinTeamByCode(teamCode, userId);
            TeamMembers newMember = teamMembersService.findMembersByTeamId(team.getId())
                    .stream()
                    .filter(tm -> tm.getUser().getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Member not found after joining"));

            TeamDiscussion discussion = teamDiscussionService.createDiscussion(
                    newMember.getId(),
                    "User joined the team!",
                    ChatMessageDTO.TeamDiscussionType.TEXT
            );
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{teamId}/regenerate-code")
    public ResponseEntity<?> regenerateTeamCode(@PathVariable Long teamId) {
        try {
            String newCode = teamService.regenerateTeamCode(teamId);
            return ResponseEntity.ok(Map.of(
                    "teamCode", newCode,
                    "message", "New team code generated successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/leave/{userId}/{teamId}")
    public ResponseEntity<?> leaveTeam(@PathVariable Long userId, @PathVariable Long teamId) {
        try {
            teamService.leaveTeam(userId, teamId);
            return ResponseEntity.ok(Map.of(
                    "message", "Successfully left the team"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Team deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(
            @PathVariable Long id,
            @RequestBody Team team) {
        try {
            team.setId(id);
            Team updatedTeam = teamService.updateTeam(team);
            return ResponseEntity.ok(updatedTeam);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/validate-code/{teamCode}")
    public ResponseEntity<?> validateTeamCode(@PathVariable String teamCode) {
        boolean isValid = teamService.isJoinCodeValid(teamCode);
        return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "message", isValid ? "Team code is valid" : "Team code is invalid or expired"
        ));
    }

    @GetMapping("/public")
    public ResponseEntity<List<Team>> getAvailablePublicTeams() {
        return ResponseEntity.ok(teamService.getAvailablePublicTeams());
    }
}