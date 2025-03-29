package com.esprit.pi.services;

import com.esprit.pi.entities.TeamDiscussion;
import com.esprit.pi.repositories.TeamDiscussionRepository;
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
public class TeamDiscussionService implements ITeamDiscussionService {

    @Autowired
    private TeamDiscussionRepository teamDiscussionRepository;

    @Override
    public List<TeamDiscussion> displayTeamDiscussions() {
        return teamDiscussionRepository.findAll();
    }

    @Override
    public TeamDiscussion addTeamDiscussion(TeamDiscussion teamDiscussion) {
        return teamDiscussionRepository.save(teamDiscussion);
    }

    @Override
    public void deleteTeamDiscussion(long id) {
        teamDiscussionRepository.deleteById(id);
    }

    @Override
    public TeamDiscussion updateTeamDiscussion(TeamDiscussion teamDiscussion) {
        return teamDiscussionRepository.save(teamDiscussion);
    }

    @Override
    public TeamDiscussion findTeamDiscussionById(long id) {
        return teamDiscussionRepository.findById(id).orElseThrow(() -> new RuntimeException("TeamDiscussion not found"));
    }
}