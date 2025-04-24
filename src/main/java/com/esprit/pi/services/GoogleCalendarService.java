package com.esprit.pi.services;

import com.esprit.pi.entities.GoogleCalendarToken;
import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.GoogleCalendarTokenRepository;
import com.esprit.pi.repositories.IHackathonRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "HackathonNet";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CALENDAR_SCOPE = CalendarScopes.CALENDAR;
    private static final String REDIRECT_URI = "http://localhost:9100/api/auth/google/callback";

    @Autowired
    private GoogleCalendarTokenRepository tokenRepository;

    @Autowired
    private IHackathonRepository hackathonRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    /**
     * Get the Google authorization URL
     */
    public String getAuthorizationUrl(Long userId) {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + clientId +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_type=code" +
                "&scope=" + CALENDAR_SCOPE +
                "&access_type=offline" +
                "&prompt=consent" +
                "&state=" + userId; // Pass userId in state
    }

    /**
     * Exchange authorization code for tokens
     */
    @Transactional
    public void exchangeCodeForTokens(String authorizationCode, User user) throws IOException {
        try {
            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    transport,
                    JSON_FACTORY,
                    clientId,
                    clientSecret,
                    authorizationCode,
                    REDIRECT_URI)
                    .execute();

            // Save the tokens
            saveToken(
                    user,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getExpiresInSeconds()
            );
        } catch (Exception e) {
            throw new IOException("Failed to exchange code for tokens: " + e.getMessage(), e);
        }
    }

    /**
     * Save or update a token
     */
    @Transactional
    public void saveToken(User user, String accessToken, String refreshToken, Long expiresIn) {
        GoogleCalendarToken token = tokenRepository.findByUser(user)
                .orElse(new GoogleCalendarToken());

        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setExpiresIn(expiresIn);

        // Only update refresh token if a new one is provided
        if (refreshToken != null && !refreshToken.isEmpty()) {
            token.setRefreshToken(refreshToken);
        }

        // Calculate expiration timestamp
        if (expiresIn != null) {
            long expiresAt = System.currentTimeMillis() + (expiresIn * 1000);
            token.setExpiresAt(new Date(expiresAt));
        }

        tokenRepository.save(token);
    }

    /**
     * Check if a user has a valid token
     */
    public boolean hasValidToken(User user) {
        Optional<GoogleCalendarToken> tokenOpt = tokenRepository.findByUser(user);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        GoogleCalendarToken token = tokenOpt.get();
        if (token.getAccessToken() == null || token.getRefreshToken() == null) {
            return false;
        }

        Date expiresAt = token.getExpiresAt();
        if (expiresAt == null) {
            return false;
        }

        boolean isValid = expiresAt.after(new Date());
        if (!isValid) {
            return refreshTokenIfNeeded(user);
        }

        return true;
    }

    /**
     * Refresh an expired token
     */
    @Transactional
    public boolean refreshTokenIfNeeded(User user) {
        GoogleCalendarToken token = tokenRepository.findByUser(user).orElse(null);

        if (token == null || token.getRefreshToken() == null) {
            return false;
        }

        // Check if token is expired or about to expire (5 minutes buffer)
        Date expiresAt = token.getExpiresAt();
        if (expiresAt == null || expiresAt.before(new Date(System.currentTimeMillis() + 300000))) {
            try {
                // Refresh the token
                GoogleTokenResponse response = new GoogleRefreshTokenRequest(
                        new NetHttpTransport(),
                        JSON_FACTORY,
                        token.getRefreshToken(),
                        clientId,
                        clientSecret)
                        .execute();

                // Update the token in database
                saveToken(
                        user,
                        response.getAccessToken(),
                        null, // Don't update refresh token
                        response.getExpiresInSeconds()
                );

                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get a Google Calendar service instance
     */
    public Calendar getCalendarService(User user) throws GeneralSecurityException, IOException {
        GoogleCalendarToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No Google Calendar token found for user"));

        // Refresh token if needed
        refreshTokenIfNeeded(user);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(token.getAccessToken())
                .setRefreshToken(token.getRefreshToken());

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Create a calendar event
     */
    public String createEvent(User user, String title, String description,
                              LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws GeneralSecurityException, IOException {

        // Ensure token is valid
        if (!refreshTokenIfNeeded(user)) {
            throw new RuntimeException("Unable to refresh access token");
        }

        Calendar calendarService = getCalendarService(user);

        Event event = new Event()
                .setSummary(title)
                .setDescription(description);

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        event.setStart(new EventDateTime().setDateTime(new DateTime(startDate)));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(endDate)));

        // Insert the event
        Event createdEvent = calendarService.events()
                .insert("primary", event)
                .execute();

        return createdEvent.getId();
    }

    /**
     * Add a hackathon event to the calendar
     */
    public String addHackathonEventToCalendar(User user, Long hackathonId, int numberOfTeams)
            throws GeneralSecurityException, IOException {

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        String eventTitle = "Hackathon: " + hackathon.getTitle();
        String eventDescription = "Mentoring " + numberOfTeams + " teams at " + hackathon.getTitle();

        // Convert Date to LocalDateTime
        LocalDateTime startDateTime = hackathon.getStartDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime endDateTime = hackathon.getEndDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return createEvent(user, eventTitle, eventDescription, startDateTime, endDateTime);
    }



}