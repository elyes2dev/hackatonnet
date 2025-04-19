package com.esprit.pi.services;

import com.esprit.pi.dtos.ChatMessageDTO;
import com.esprit.pi.entities.TeamDiscussion;
import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.repositories.TeamDiscussionRepository;
import com.esprit.pi.repositories.TeamMembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamDiscussionService implements ITeamDiscussionService {

    @Autowired
    private TeamDiscussionRepository teamDiscussionRepository;

    @Autowired
    private TeamMembersRepository teamMembersRepository;

    @Override
    @Transactional
    public TeamDiscussion createDiscussion(Long teamMemberId, String message) {
        return createDiscussion(teamMemberId, message, ChatMessageDTO.TeamDiscussionType.TEXT);
    }

    @Override
    @Transactional
    public TeamDiscussion createDiscussion(Long teamMemberId, String message,
                                           ChatMessageDTO.TeamDiscussionType messageType) {
        if (teamMemberId == null || message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Team member ID and message are required");
        }

        TeamMembers teamMember = teamMembersRepository.findById(teamMemberId)
                .orElseThrow(() -> new RuntimeException("Team member not found: " + teamMemberId));

        TeamDiscussion discussion = new TeamDiscussion();
        discussion.setTeam(teamMember.getTeam());
        discussion.setTeamMember(teamMember);
        discussion.setMessage(message);
        discussion.setMessageType(convertMessageType(messageType));
        discussion.setIsRead(false);

        return teamDiscussionRepository.save(discussion);
    }

    @Override
    public List<TeamDiscussion> getTeamDiscussions(Long teamId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }
        return teamDiscussionRepository.findByTeamIdOrderByCreatedAtDesc(teamId);
    }

    @Override
    public List<TeamDiscussion> getMemberDiscussions(Long teamMemberId) {
        if (teamMemberId == null) {
            throw new IllegalArgumentException("Team Member ID cannot be null");
        }
        return teamDiscussionRepository.findByTeamMemberIdOrderByCreatedAtDesc(teamMemberId);
    }

    @Override
    @Transactional
    public TeamDiscussion updateDiscussion(Long discussionId, String message) {
        if (discussionId == null || message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Discussion ID and message are required");
        }

        TeamDiscussion discussion = teamDiscussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found with id: " + discussionId));
        discussion.setMessage(message);
        discussion.setIsRead(false);
        return teamDiscussionRepository.save(discussion);
    }

    @Override
    @Transactional
    public void deleteDiscussion(Long discussionId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("Discussion ID cannot be null");
        }
        teamDiscussionRepository.deleteById(discussionId);
    }

    @Override
    public TeamDiscussion getDiscussionById(Long discussionId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("Discussion ID cannot be null");
        }
        return teamDiscussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found with id: " + discussionId));
    }

    @Override
    @Transactional
    public TeamDiscussion markAsRead(Long discussionId) {
        if (discussionId == null) {
            throw new IllegalArgumentException("Discussion ID cannot be null");
        }

        TeamDiscussion discussion = teamDiscussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Discussion not found with id: " + discussionId));
        discussion.setIsRead(true);
        return teamDiscussionRepository.save(discussion);
    }

    @Override
    public Long getUnreadCount(Long teamId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team ID cannot be null");
        }
        return teamDiscussionRepository.countByTeamIdAndIsReadFalse(teamId);
    }

    private TeamDiscussion.MessageType convertMessageType(ChatMessageDTO.TeamDiscussionType type) {
        if (type == null) return TeamDiscussion.MessageType.TEXT;
        return switch (type) {
            case TEXT -> TeamDiscussion.MessageType.TEXT;
            case IMAGE -> TeamDiscussion.MessageType.IMAGE;
            case FILE -> TeamDiscussion.MessageType.FILE;
            case EMOJI -> TeamDiscussion.MessageType.EMOJI;
        };
    }
}