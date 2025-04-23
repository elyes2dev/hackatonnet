package com.esprit.pi.controllers;

import com.esprit.pi.dto.EmailRequest;
import com.esprit.pi.dto.SocialLoginRequest;
import com.esprit.pi.dtos.PasswordDto;
import com.esprit.pi.entities.Role;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.PasswordResetTokenRepository;
import com.esprit.pi.repositories.RoleRepository;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.EmailService;
import com.esprit.pi.services.JwtUtility;
import com.esprit.pi.services.UserService;
import com.esprit.pi.services.VerificationService;
import com.esprit.pi.utility.GoogleTokenVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserController userController;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            log.info("Signup attempt for user: {}", user.getName());
            userController.createUser(user);
            response.put("message", "User registered successfully");
        } catch (Exception e) {
            log.error("Signup failed for user: {}. Reason: {}", user.getName(), e.getMessage(), e);
            response.put("error", "Signup failed");
        }
        return response;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User loginUser) {
        Map<String, String> response = new HashMap<>();
        try {
            log.info("Login attempt for user: {}", loginUser.getName());
            User userOptional = userRepository.findByName(loginUser.getName());

            if (userOptional != null && encoder.matches(loginUser.getPassword(), userOptional.getPassword()))
            {
                List<String> roleNames = userOptional.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());

                String rolesJson = new ObjectMapper().writeValueAsString(roleNames);
                Map<String, String> claims = new HashMap<>();
                claims.put("roles", rolesJson);
                claims.put("userid", String.valueOf(userOptional.getId()));

                String token = jwtUtility.generateToken(claims, userOptional.getName(), 24 * 60 * 60 * 1000);
                response.put("jwtToken", token);
                log.info("Login successful for user: {}", loginUser.getName());
            } else {
                log.warn("Login failed. User not found: {}", loginUser.getName());
                response.put("error", "Invalid credentials");
            }
        } catch (Exception e) {
            log.error("Login error for user: {}. Reason: {}", loginUser.getName(), e.getMessage(), e);
            response.put("error", "Login failed");
        }
        return response;
    }

    @PostMapping("/reset_password")
    public Map<String, String> resetPassword(@RequestBody EmailRequest request) {
        String email = request.getEmail();

        Map<String, String> response = new HashMap<>();
        try {
            log.info("Password reset requested for email: {}", email);
            User user = userService.findUserByEmail(email);

            if (user == null) {
                log.warn("Password reset failed. No user found for email: {}", email);
                response.put("message", "User not Found!");
                return response;
            }

            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            emailService.sendRecoveryEmail(email, token);
            response.put("message", "We've sent a recovery mail!");
            log.info("Recovery email sent to: {}", email);

        } catch (Exception e) {
            log.error("Password reset error for email: {}. Reason: {}", email, e.getMessage(), e);
            response.put("error", "Failed to send recovery email");
        }
        return response;
    }

    @GetMapping("/changePassword")
    public RedirectView redirectToResetPasswordPage(@RequestParam("token") String token) {
        try {
            log.info("Change password requested with token: {}", token);
            String result = verificationService.validatePasswordResetToken(token);

            if (result != null) {
                log.warn("Token validation failed: {}", result);
                return new RedirectView("http://localhost:4200/#/auth/error");
            } else {
                passwordResetTokenRepository.delete(passwordResetTokenRepository.findByToken(token));
                return new RedirectView("http://localhost:4200/save-password?token=" + token);
            }
        } catch (Exception e) {
            log.error("Error during token validation. Token: {}, Reason: {}", token, e.getMessage(), e);
            return new RedirectView("http://localhost:4200/error/server");
        }
    }


    @PostMapping("/savePassword")
    public Map<String, String> savePassword(@Valid @RequestBody PasswordDto passwordDto) {
        Map<String, String> response = new HashMap<>();
        try {
            log.info("Attempt to save new password with token: {}", passwordDto.getToken());

            if (passwordDto.getToken() == null) {
                log.warn("Null token in password save request");
                throw new IllegalArgumentException("Token cannot be null");
            }

            String result = verificationService.validatePasswordResetToken(passwordDto.getToken());

            if (Objects.equals(result, "expired")) {
                log.warn("Token expired: {}", passwordDto.getToken());
                response.put("message", "Expired!");
                return response;
            }

            User user = userService.getUserByPasswordResetToken(passwordDto.getToken());

            if (user != null) {
                userService.changeUserPassword(user, passwordDto.getNewPassword(), passwordDto.getToken());
                response.put("message", "Success!");
                log.info("Password changed successfully for user: {}", user.getEmail());
            } else {
                log.warn("No user found for token: {}", passwordDto.getToken());
                response.put("message", "Fail!");
            }

        } catch (Exception e) {
            log.error("Error saving password. Token: {}, Reason: {}", passwordDto.getToken(), e.getMessage(), e);
            response.put("error", "Internal server error");
        }
        return response;
    }

    @PostMapping("/social-login")
    public ResponseEntity<Void> socialLogin(@RequestBody SocialLoginRequest request) {
        // 1. Verify Google token & load/create user…
        GoogleIdToken.Payload payload = GoogleTokenVerifier.verifyToken(request.getCredential());
        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = payload.getEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName((String) payload.get("name"));// Optional default role
            user = userRepository.save(newUser);  // <-- Fix: assign saved user back to user
        }

        // 2. Generate your JWT
        Map<String,String> claims = Map.of("role", String.valueOf(user.getRoles()));
        String jwt = jwtUtility.generateToken(claims, email, 86_400_000);

        // 3. Build a Set-Cookie header for an HttpOnly, Secure cookie
        ResponseCookie cookie = ResponseCookie.from("JWT", jwt)                  // cookie name & value
                .httpOnly(true)                                                      // not accessible in JS :contentReference[oaicite:0]{index=0}
                .secure(true)                                                        // only over HTTPS (in prod)
                .path("/")                                                           // sent to all paths
                .maxAge(86_400)                                                      // 1 day in seconds
                .sameSite("Strict")                                                  // mitigate CSRF
                .build();

        // 4. Redirect to your Angular app’s root URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())                   // set the JWT cookie :contentReference[oaicite:1]{index=1}
                .location(URI.create("http://localhost:4200/"))                      // 302 → Angular root
                .build();
    }


}

