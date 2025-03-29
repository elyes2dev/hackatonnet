package com.esprit.pi.services;

import com.esprit.pi.entities.TeamDiscussion;

import java.util.List;

public interface ITeamDiscussionService {
    List<TeamDiscussion> displayTeamDiscussions();
    TeamDiscussion addTeamDiscussion(TeamDiscussion teamDiscussion);
    void deleteTeamDiscussion(long id);
    TeamDiscussion updateTeamDiscussion(TeamDiscussion teamDiscussion);
    TeamDiscussion findTeamDiscussionById(long id);
}