package com.esprit.pi.controllers;

import com.esprit.pi.entities.Team;
import com.esprit.pi.services.ITeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private ITeamService teamService;

    @GetMapping("/all")
    public List<Team> getAllTeams() {
        return teamService.displayTeams();
    }
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Team Controller is working!");
    }
   /* @GetMapping("/{id}")
    public Team getTeamById(@PathVariable Long id) {
        return teamService.updateTeam(team);
    }*/

    @PostMapping("/add")
    public Team addTeam(@RequestBody Team team) {
        return teamService.addTeam(team);
    }

    @PutMapping("/update/{id}")
    public Team updateTeam(@PathVariable Long id, @RequestBody Team team) {
        return teamService.updateTeam(team);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
    }
}