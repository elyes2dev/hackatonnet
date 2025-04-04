package com.esprit.pi.controllers;


import com.esprit.pi.entities.User;
import com.esprit.pi.entities.Workshop;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.IWorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/workshops")
public class WorkshopController {

    @Autowired
    private IWorkshopService workshopService;

    @Autowired
    private UserRepository userRepository; // Ensure this repository is injected

    // Add a workshop with a static user with ID 1
    @PostMapping("/add")
    public ResponseEntity<Workshop> addWorkshop(@RequestBody Workshop workshop) {
        // Fetch the user with ID 1
        Optional<User> userOptional = userRepository.findById(1L);  // Use the static user ID

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            workshop.setUser(user);  // Associate the workshop with the user
            Workshop savedWorkshop = workshopService.addWorkshop(workshop);
            return ResponseEntity.ok(savedWorkshop);
        } else {
            return ResponseEntity.notFound().build();  // If user not found, return 404
        }
    }
    @GetMapping("/{id}")
    public Optional<Workshop> getWorkshopById(@PathVariable Long id) {
        return workshopService.findById(id);
    }

    @GetMapping
    public List<Workshop> getAllWorkshops() {
        return workshopService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteWorkshop(@PathVariable Long id) {
        workshopService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workshop> updateWorkshop(@PathVariable Long id, @RequestBody Workshop workshop) {
        try {
            Workshop updated = workshopService.updateWorkshop(id, workshop);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // If workshop not found
        }
    }
}

