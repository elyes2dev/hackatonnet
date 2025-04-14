package com.esprit.pi.controllers;

import com.esprit.pi.dtos.PasswordDto;
import com.esprit.pi.entities.PasswordResetToken;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.PasswordResetTokenRepository;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.EmailService;
import com.esprit.pi.services.JwtUtility;
import com.esprit.pi.services.UserService;
import com.esprit.pi.services.VerificationService;
import com.esprit.pi.utilitys.VerificationCodeGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody User user) {
        // Save user to database
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and generate JWT token",
            description = "Provide username and password to receive a JWT token for authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginUser) {
        User user = userRepository.findByName(loginUser.getName());

        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(401).body(response);
        }

        if (loginUser.getPassword() != null && !loginUser.getPassword().equals(user.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid password");
            return ResponseEntity.status(401).body(response);
        }

        Map<String, String> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtility.generateToken(claims, loginUser.getName(), 24 * 60 * 60 * 1000);

        Map<String, String> response = new HashMap<>();
        response.put("jwtToken", token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login")
    @Operation(summary = "Get login instructions",
            description = "Returns instructions to use POST /login instead.")
    @ApiResponse(responseCode = "405", description = "Method not allowed",
            content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, String>> getLogin() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Use POST /login with username and password to authenticate");
        return ResponseEntity.status(405).body(response);
    }
    @PostMapping("/reset_password")
    public Map<String, String> resetPassword(@RequestBody String email){

        User user = userService.findUserByEmail(email);


        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not Found!");
            return response;
        }

        String sendTo = email.trim();
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        emailService.sendRecoveryEmail(email ,token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "We've sent a recovery mail!");
        return response;
    }

    @GetMapping("/changePassword")
    public Map<String, String>  showChangePasswordPage(@RequestParam("token") String token) {
        String result = verificationService.validatePasswordResetToken(token);
        System.out.println(result);
        if(result != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "oops!Somethin went wrong");
            return response;

        } else {

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password Will be resetted!");
            return response;
        }
    }

    @PostMapping("/savePassword")
    public Map<String, String> savePassword(@Valid @RequestBody PasswordDto passwordDto) {
        if (passwordDto.getToken() == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        String result = verificationService.validatePasswordResetToken(passwordDto.getToken());

        if(Objects.equals(result, "expired")) {
            Map<String, String> response = new HashMap<>();
            response.put("message" , "Expired!");
            return response;
        }

        User user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user != null) {
            userService.changeUserPassword(user, passwordDto.getNewPassword(),passwordDto.getToken());
            Map<String, String> response = new HashMap<>();
            response.put("message" , "Succes!");
            return response;

        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message" , "Fail!");
            return response;
        }
    }
}
