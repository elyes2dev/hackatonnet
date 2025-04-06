package com.esprit.pi.controllers;


import com.esprit.pi.entities.User;
import com.esprit.pi.entities.Workshop;
import com.esprit.pi.repositories.UserRepository;
import com.esprit.pi.services.IWorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/add")
    public ResponseEntity<Workshop> addWorkshop(@RequestBody Workshop workshop) {
        if (workshop.getUser() == null) {
            return ResponseEntity.badRequest().body(null); // No user provided
        }

        // Fetch the user from the database to make sure it's already persisted
        User user = userRepository.findById(workshop.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Set the user to the workshop
        workshop.setUser(user);

        // Save the workshop
        Workshop savedWorkshop = workshopService.addWorkshop(workshop);
        return ResponseEntity.ok(savedWorkshop);
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

