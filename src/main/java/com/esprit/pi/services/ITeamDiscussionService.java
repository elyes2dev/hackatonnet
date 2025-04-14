package com.esprit.pi.services;

import com.esprit.pi.dtos.ChatMessageDTO;
import com.esprit.pi.entities.TeamDiscussion;

import java.util.List;

public interface ITeamDiscussionService {
    TeamDiscussion createDiscussion(Long teamMemberId, String message);
    TeamDiscussion createDiscussion(Long teamMemberId, String message, ChatMessageDTO.TeamDiscussionType messageType);
    List<TeamDiscussion> getTeamDiscussions(Long teamId);
    List<TeamDiscussion> getMemberDiscussions(Long teamMemberId);
    TeamDiscussion updateDiscussion(Long discussionId, String message);
    void deleteDiscussion(Long discussionId);
    TeamDiscussion getDiscussionById(Long discussionId);
    TeamDiscussion markAsRead(Long discussionId);
    Long getUnreadCount(Long teamId);
}