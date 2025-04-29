package com.esprit.pi.controllers;

import com.esprit.pi.entities.Role;
import com.esprit.pi.entities.User;
import com.esprit.pi.repositories.RoleRepository;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.esprit.pi.services.UserService;


import java.beans.Encoder;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Process roles correctly
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                Set<Role> managedRoles = new HashSet<>();

                for (Role role : user.getRoles()) {
                    // Find the role by name from the repository
                    Role managedRole = roleRepository.findByName(role.getName());

                    managedRoles.add(managedRole);
                }

                // Replace with managed entities
                user.setRoles(managedRoles);
            }

            // Now save the user
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/refresh-points")
    public User refreshUserBadge(@PathVariable Long id) {
        return userService.updateUserPointsAndBadge(id);
    }
}

