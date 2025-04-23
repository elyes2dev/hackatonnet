package com.esprit.pi.controllers;

import com.esprit.pi.entities.User;
import com.esprit.pi.services.DataExportService;
import com.esprit.pi.services.MentorMatchingService;
import com.esprit.pi.services.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentor-recommendations")
@RequiredArgsConstructor
public class MentorRecommendationController {

    private final DataExportService dataExportService;
    private final MentorMatchingService mentorMatchingService;
    private final TeamService teamService;
    private final ObjectMapper objectMapper;

    /**
     * Get recommended mentors for a specific team
     * @param teamId The ID of the team
     * @param hackathonId The ID of the hackathon
     * @return List of mentor recommendations
     */
    @PostMapping("/mentors/{teamId}")
    public ResponseEntity<?> getMentorRecommendations(@PathVariable Long teamId,
                                                      @RequestParam Long hackathonId) {
        try {
            String response = dataExportService.getMentorRecommendations(teamId, hackathonId);
            Map<String, Object> parsedResponse = objectMapper.readValue(response, Map.class);
            return ResponseEntity.ok(parsedResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get recommendations: " + e.getMessage()));
        }
    }

    /**
     * Get all available mentors for a specific hackathon
     * @param hackathonId The ID of the hackathon
     * @return List of mentor users
     */
    @GetMapping("/hackathon/{hackathonId}/mentors")
    public ResponseEntity<List<User>> getAvailableMentorsForHackathon(@PathVariable Long hackathonId) {
        try {
            List<User> mentors = mentorMatchingService.getAvailableMentorsForHackathon(hackathonId);
            return ResponseEntity.ok(mentors);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Manually match a mentor to a team
     * @param teamId The ID of the team
     * @param requestBody Request containing mentorId
     * @return Success message*/

    @PostMapping("/team/{teamId}/assign")
    public ResponseEntity<Map<String, String>> assignMentorToTeam(
            @PathVariable Long teamId,
            @RequestBody Map<String, Long> requestBody) {
        try {
            Long mentorId = requestBody.get("mentorId");
            if (mentorId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "mentorId is required"));
            }
            teamService.assignMentorToTeam(teamId, mentorId);
            return ResponseEntity.ok(Map.of("message", "Mentor successfully assigned to team"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to assign mentor: " + e.getMessage()));
        }
    }
}