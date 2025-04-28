package com.esprit.pi.controllers;

import com.esprit.pi.dtos.TeamMembershipDTO;
import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.services.ITeamMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;




import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/team-members")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TeamMembersController {

    private final ITeamMembersService teamMembersService;

    @Autowired
    public TeamMembersController(ITeamMembersService teamMembersService) {
        this.teamMembersService = teamMembersService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TeamMembers>> getAllTeamMembers() {
        return ResponseEntity.ok(teamMembersService.displayTeamMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMembers> getTeamMemberById(@PathVariable Long id) {
        try {
            TeamMembers teamMember = teamMembersService.findTeamMemberById(id);
            return ResponseEntity.ok(teamMember);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<TeamMembers> addTeamMember(@RequestBody TeamMembers teamMembers) {
        try {
            TeamMembers addedMember = teamMembersService.addTeamMember(teamMembers);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMembers>> getTeamMembersByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamMembersService.findMembersByTeamId(teamId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TeamMembers> updateTeamMember(@PathVariable Long id,
                                                        @RequestBody TeamMembers teamMembers) {
        try {
            teamMembers.setId(id);
            TeamMembers updatedMember = teamMembersService.updateTeamMember(teamMembers);
            return ResponseEntity.ok(updatedMember);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTeamMember(@PathVariable Long id) {
        try {
            teamMembersService.deleteTeamMember(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Team member deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamMembershipDTO>> getTeamMembershipsByUserId(@PathVariable Long userId) {
        try {
            List<TeamMembers> teamMembers = teamMembersService.findAllTeamMembersByUserId(userId);
            List<TeamMembershipDTO> dtos = teamMembers.stream()
                    .map(TeamMembershipDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }
}