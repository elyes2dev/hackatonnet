package com.esprit.pi.controllers;

import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.services.ITeamMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-members")
public class TeamMembersController {

    @Autowired
    private ITeamMembersService teamMembersService;

    @GetMapping("/all")
    public List<TeamMembers> getAllTeamMembers() {
        return teamMembersService.displayTeamMembers();
    }

    @GetMapping("/{id}")
    public TeamMembers getTeamMemberById(@PathVariable Long id) {
        return teamMembersService.findTeamMemberById(id);
    }

    @PostMapping("/add")
    public TeamMembers addTeamMember(@RequestBody TeamMembers teamMembers) {
        return teamMembersService.addTeamMember(teamMembers);
    }

    @PutMapping("/update/{id}")
    public TeamMembers updateTeamMember(@PathVariable Long id, @RequestBody TeamMembers teamMembers) {
        return teamMembersService.updateTeamMember(teamMembers);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTeamMember(@PathVariable Long id) {
        teamMembersService.deleteTeamMember(id);
    }
}