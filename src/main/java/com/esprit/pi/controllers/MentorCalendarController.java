package com.esprit.pi.controllers;

import com.esprit.pi.entities.ListMentor;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.ListMentorRepository;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mentor-calendar")
public class MentorCalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Autowired
    private ListMentorRepository listMentorRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl(@RequestParam Long userId) {
        try {
            // Get the user by ID
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String authUrl = googleCalendarService.getAuthorizationUrl(user.getId());

            Map<String, String> response = new HashMap<>();
            response.put("url", authUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Status endpoint without requiring mentorListingId - just check if user is connected
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCalendarStatus(@RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean isConnected = googleCalendarService.hasValidToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("connected", isConnected);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Specific mentor listing status endpoint
    @GetMapping("/status/{listMentorId}")
    public ResponseEntity<Map<String, Object>> getCalendarSyncStatus(
            @PathVariable Long listMentorId,
            @RequestParam Long userId) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ListMentor listMentor = listMentorRepository.findById(listMentorId)
                    .orElseThrow(() -> new RuntimeException("Mentor listing not found"));

            // Check if the user is the mentor of this listing
            if (!listMentor.getMentor().getId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to access this mentor listing");
            }

            boolean isConnected = googleCalendarService.hasValidToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("connected", isConnected);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncHackathonToCalendar(
            @RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long hackathonId = Long.valueOf(requestBody.get("hackathonId").toString());
            Integer numberOfTeams = Integer.valueOf(requestBody.get("numberOfTeams").toString());

            if (!googleCalendarService.hasValidToken(user)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Google Calendar not connected");
                response.put("authUrl", googleCalendarService.getAuthorizationUrl(user.getId()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String eventId = googleCalendarService.addHackathonEventToCalendar(user, hackathonId, numberOfTeams);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", eventId);
            response.put("message", "Hackathon event added to Google Calendar");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to add event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}