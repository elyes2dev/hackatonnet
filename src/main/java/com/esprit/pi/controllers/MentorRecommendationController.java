package com.esprit.pi.controllers;

import com.esprit.pi.dtos.MentorRecommendationDTO;
import com.esprit.pi.entities.ListMentor;
import com.esprit.pi.entities.TeamMembers;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.ListMentorRepository;
import com.esprit.pi.repositories.TeamMembersRepository;
import com.esprit.pi.services.DataExportService;
import com.esprit.pi.services.MentorMatchingService;
import com.esprit.pi.services.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentor-recommendations")
@RequiredArgsConstructor
public class MentorRecommendationController {

    private final DataExportService dataExportService;
    private final MentorMatchingService mentorMatchingService;
    private final TeamService teamService;
    private final ObjectMapper objectMapper;
    private final ListMentorRepository listMentorRepository;
    private final TeamMembersRepository teamMembersRepository;


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



    /**
     * Check if a mentor is available for assignment in a hackathon
     *
     * @param mentorId The ID of the mentor
     * @param hackathonId The ID of the hackathon
     * @return Availability status with current and maximum team counts
     */
    @GetMapping("/mentors/{mentorId}/availability")
    public ResponseEntity<?> checkMentorAvailability(
            @PathVariable Long mentorId,
            @RequestParam Long hackathonId) {

        // Find the mentor's maximum team count from ListMentor
        Optional<ListMentor> mentorList = listMentorRepository.findByMentorIdAndHackathonId(mentorId, hackathonId);

        if (mentorList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "available", false,
                            "currentTeams", 0,
                            "maxTeams", 0,
                            "message", "Mentor has not registered for this hackathon"
                    ));
        }

        int maxTeams = mentorList.get().getNumberOfTeams();

        // Count current teams the mentor is part of in this hackathon
        int currentTeams = teamMembersRepository.countByUserIdAndRoleAndTeamHackathonId(
                mentorId,
                TeamMembers.Role.MENTOR,
                hackathonId
        );

        boolean isAvailable = currentTeams < maxTeams;

        return ResponseEntity.ok(Map.of(
                "available", isAvailable,
                "currentTeams", currentTeams,
                "maxTeams", maxTeams
        ));
    }

    /**
     * Get available mentors with recommendations for a team
     *
     * @param teamId The ID of the team
     * @param hackathonId The ID of the hackathon
     * @return List of recommended mentors who are still available
     */
    @PostMapping("/available-mentors/{teamId}")
    public ResponseEntity<?> getAvailableMentorRecommendations(
            @PathVariable Long teamId,
            @RequestParam Long hackathonId) {

        // Get all recommendations first
        List<MentorRecommendationDTO> recommendedMentors = mentorMatchingService.getRecommendedMentorsForTeam(teamId, hackathonId);

        // Filter to only include available mentors
        List<MentorRecommendationDTO> availableMentors = recommendedMentors.stream()
                .filter(mentorDTO -> {
                    Optional<ListMentor> mentorList = listMentorRepository.findByMentorIdAndHackathonId(
                            mentorDTO.getMentorId(), hackathonId
                    );

                    if (mentorList.isEmpty()) {
                        return false;
                    }

                    int maxTeams = mentorList.get().getNumberOfTeams();
                    int currentTeams = teamMembersRepository.countByUserIdAndRoleAndTeamHackathonId(
                            mentorDTO.getMentorId(),
                            TeamMembers.Role.MENTOR,
                            hackathonId
                    );

                    return currentTeams < maxTeams;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "recommendedMentors", availableMentors
        ));
    }

}

