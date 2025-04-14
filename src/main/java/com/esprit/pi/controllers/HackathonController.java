// src/main/java/com/yourpackage/controller/HackathonController.java
package com.esprit.pi.controllers;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.repositories.HackathonRepository;
import com.esprit.pi.services.HackathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pi/api/hackathons")
@CrossOrigin(origins = "http://localhost:4200")
public class HackathonController {
    private final HackathonService hackathonService;

    public HackathonController(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    @GetMapping
    public List<Hackathon> getAllHackathons() {
        return hackathonService.getAllHackathons();
    }
}