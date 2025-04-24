package com.esprit.pi.controllers;

import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/auth/google")
public class GoogleAuthCallbackController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/callback")
    public RedirectView handleGoogleCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {

        try {
            // The state parameter contains the user ID
            Long userId = Long.parseLong(state);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Exchange code for tokens
            googleCalendarService.exchangeCodeForTokens(code, user);

            // Redirect to a success page or close the popup
            return new RedirectView("/calendar-connected.html");
        } catch (Exception e) {
            // Redirect to error page
            return new RedirectView("/calendar-error.html");
        }
    }
}