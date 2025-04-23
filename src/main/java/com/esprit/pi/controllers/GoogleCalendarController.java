package com.esprit.pi.controllers;

import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.GoogleCalendarService;
import com.esprit.pi.services.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/google")
public class GoogleCalendarController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Autowired
    private JwtUtility jwtUtility;

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl(
            @RequestParam("userId") Long userId) {
        String redirectUri = "http://localhost:9100/api/auth/google/callback";

        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/calendar" +
                "&access_type=offline" +
                "&prompt=consent" +
                "&state=" + userId; // Pass userId in state

        Map<String, String> response = new HashMap<>();
        response.put("url", authUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleOAuthCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {

        try {
            if (state == null || state.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing state parameter with user ID.");
            }

            Long userId = Long.parseLong(state);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Exchange the authorization code for tokens
            googleCalendarService.exchangeCodeForTokens(code, user);

            // Return a success page that closes the popup and notifies the frontend
            return ResponseEntity.ok(
                    "<!DOCTYPE html><html><head><title>Authorization Successful</title>" +
                            "<script>window.onload = function() {" +
                            "  window.opener.postMessage('google-auth-success', '*');" +
                            "  window.close();" +
                            "}</script></head><body>" +
                            "<h2>Google Calendar Authorization Successful!</h2>" +
                            "<p>You can close this window now.</p></body></html>"
            );
        } catch (Exception e) {
            System.err.println("OAuth callback error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to authorize: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> checkGoogleCalendarStatus(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            boolean isConnected = googleCalendarService.hasValidToken(user);
            return ResponseEntity.ok(Map.of("connected", isConnected));
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @PostMapping("/add-event")
    public ResponseEntity<?> addEventToCalendar(
            @RequestParam("hackathonId") Long hackathonId,
            @RequestParam("numberOfTeams") int numberOfTeams,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

            if (!userOptional.isPresent()) {
                throw new RuntimeException("User not found");
            }

            User user = userOptional.get();

            if (!googleCalendarService.hasValidToken(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Google Calendar not connected"));
            }

            googleCalendarService.addHackathonToCalendar(user, hackathonId, numberOfTeams);

            return ResponseEntity.ok(Map.of("success", true, "message", "Event added to calendar"));
        } catch (Exception e) {
            System.err.println("Error adding event to calendar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to add event: " + e.getMessage()));
        }
    }}