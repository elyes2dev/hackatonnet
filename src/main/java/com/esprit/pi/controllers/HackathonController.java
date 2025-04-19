package com.esprit.pi.controllers;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.repositories.HackathonRepository;
import com.esprit.pi.services.IHackathonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/hackathons")
public class HackathonController {
    @Autowired
    IHackathonService hackathonService;



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
