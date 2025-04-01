package com.esprit.pi.controllers;

import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody User user) {
        // Save user to database
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User loginUser) {
        User userOptional = userRepository.findByName(loginUser.getName());


            Map<String, String> claims = new HashMap<>();
            claims.put("role", "USER");

            String token = jwtUtility.generateToken(claims, loginUser.getName(), 24 * 60 * 60 * 1000);

            Map<String, String> response = new HashMap<>();
            response.put("jwtToken", token);
            return response;



    }
}
