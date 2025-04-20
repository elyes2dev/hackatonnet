package com.esprit.pi.controllers;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.repositories.IHackathonRepository;
import com.esprit.pi.services.IHackathonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/pi/hackathons")
public class HackathonController {
    @Autowired
    IHackathonService hackathonService;


    @PostMapping("/create-hackathon")
    public ResponseEntity<?> createHackathon(@RequestBody Hackathon hackathon) {
        try {
            if (hackathon.getCreatedBy() == null || hackathon.getCreatedBy().getId() == null) {
                return ResponseEntity.badRequest().body("Error: 'createdBy' field is required.");
            }

            Hackathon savedHackathon = hackathonService.createHackathon(hackathon);
            return ResponseEntity.ok(savedHackathon);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }



    @PutMapping("/update-hackathon/{id}")
    public ResponseEntity<?> updateHackathon(@PathVariable Long id, @RequestBody Hackathon hackathon) {
        try {
            Hackathon updatedHackathon = hackathonService.updateHackathon(id, hackathon);
            return ResponseEntity.ok(updatedHackathon);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHackathon(@PathVariable Long id) {
        try {
            hackathonService.deleteHackathon(id);
            return ResponseEntity.ok("Hackathon deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/get-hackathon/{id}")
    public ResponseEntity<?> getHackathonById(@PathVariable Long id) {
        try {
            Hackathon hackathon = hackathonService.getHackathonById(id);
            return ResponseEntity.ok(hackathon);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Hackathon>> getAllHackathons() {
        List<Hackathon> hackathons = hackathonService.getAllHackathons();
        return ResponseEntity.ok(hackathons);
    }

}
