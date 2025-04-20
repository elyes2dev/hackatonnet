package com.esprit.pi.services;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationParticipantService {
    // Map<TeamId, Set<Username>>
    private final Map<Long, Set<String>> activeParticipants = new ConcurrentHashMap<>();

    public void addParticipant(Long teamId, String username) {
        activeParticipants.computeIfAbsent(teamId, k -> ConcurrentHashMap.newKeySet()).add(username);
    }

    public void removeParticipant(Long teamId, String username) {
        Set<String> participants = activeParticipants.get(teamId);
        if (participants != null) {
            participants.remove(username);
            if (participants.isEmpty()) {
                activeParticipants.remove(teamId);
            }
        }
    }

    public boolean isParticipant(Long teamId, String username) {
        Set<String> participants = activeParticipants.get(teamId);
        return participants != null && participants.contains(username);
    }

    public Set<String> getParticipants(Long teamId) {
        return activeParticipants.getOrDefault(teamId, ConcurrentHashMap.newKeySet());
    }
}