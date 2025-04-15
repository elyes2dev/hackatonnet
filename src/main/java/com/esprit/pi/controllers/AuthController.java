package com.esprit.pi.controllers;

import com.esprit.pi.dtos.PasswordDto;
import com.esprit.pi.entities.Role;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.EmailService;
import com.esprit.pi.services.JwtUtility;
import com.esprit.pi.services.UserService;
import com.esprit.pi.services.VerificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody User user) throws JsonProcessingException {
        // Save user to database
        userRepository.save(user);


        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User loginUser) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();

            User userOptional = userRepository.findByName(loginUser.getName());
        System.out.println(userOptional.getRoles());
        if (userOptional != null) {

            Map<String, String> claims = new HashMap<>();


            List<String> roleNames = userOptional.getRoles()
                    .stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());



            ObjectMapper mapper = new ObjectMapper();
            String rolesJson = mapper.writeValueAsString(roleNames);
            claims.put("roles", rolesJson);

            claims.put("userid", String.valueOf(userOptional.getId()));

            String token = jwtUtility.generateToken(claims, userOptional.getName(), 24 * 60 * 60 * 1000);

            response.put("jwtToken", token);
        }else
        {
            response.put("Error","Error!");
        }



            return response;

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
