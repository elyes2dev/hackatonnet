package com.esprit.pi.controllers;

import com.esprit.pi.entities.TeamDiscussion;
import com.esprit.pi.services.ITeamDiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-discussions")
public class TeamDiscussionController {

    @Autowired
    private ITeamDiscussionService teamDiscussionService;

    @GetMapping("/all")
    public List<TeamDiscussion> getAllTeamDiscussions() {
        return teamDiscussionService.displayTeamDiscussions();
    }

    @GetMapping("/{id}")
    public TeamDiscussion getTeamDiscussionById(@PathVariable Long id) {
        return teamDiscussionService.findTeamDiscussionById(id);
    }

    @PostMapping("/add")
    public TeamDiscussion addTeamDiscussion(@RequestBody TeamDiscussion teamDiscussion) {
        return teamDiscussionService.addTeamDiscussion(teamDiscussion);
    }

    @PutMapping("/update/{id}")
    public TeamDiscussion updateTeamDiscussion(@PathVariable Long id, @RequestBody TeamDiscussion teamDiscussion) {
        return teamDiscussionService.updateTeamDiscussion(teamDiscussion);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTeamDiscussion(@PathVariable Long id) {
        teamDiscussionService.deleteTeamDiscussion(id);
    }
}