package com.esprit.pi.controllers;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.services.IHackathonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/hackathons")
public class HackathonController {
    @Autowired
    IHackathonService hackathonService;

    @PostMapping("/create-hackathon")
    public Hackathon createhackathon(@RequestBody Hackathon hackathon){
        return hackathonService.createHackathon(hackathon);
    }

    @PutMapping("/update-hackathon/{id}")
    public Hackathon updatehackathon(@PathVariable Long id, @RequestBody Hackathon hackathon) {
        return hackathonService.updateHackathon(id, hackathon);
    }

    @DeleteMapping("/{id}")
    public void deleteHackathon(@PathVariable Long id) {
        hackathonService.deleteHackathon(id);
    }

    @GetMapping("/get-hackathon/{id}")
    public Hackathon getHackathonById(@PathVariable Long id) {
        return hackathonService.getHackathonById(id);
    }

    @GetMapping
    public List<Hackathon> getAllHackathons() {
        return hackathonService.getAllHackathons();
    }
}
