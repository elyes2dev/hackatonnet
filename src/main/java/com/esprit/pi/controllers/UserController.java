package com.esprit.pi.controllers;

import com.esprit.pi.entities.User;
import com.esprit.pi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow calls from Angular frontend, adjust if needed
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.getUserById(id);
        return optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
    }

    // (Optional) Force re-evaluation and update of badge
    @GetMapping("/{id}/refresh-points")
    public User refreshUserBadge(@PathVariable Long id) {
        return userService.updateUserPointsAndBadge(id);
    }
}
