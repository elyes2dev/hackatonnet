package com.esprit.pi.services;

import com.esprit.pi.entities.Hackathon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HackathonService {
    public List<Hackathon> getAllHackathons() {
        List<Hackathon> hackathons = new ArrayList<>();
        hackathons.add(new Hackathon(1, "Hackathon 1", 5)); // Adjust fields as per your model
        hackathons.add(new Hackathon(2, "Hackathon 2", 4));
        return hackathons; // Replace with DB call if applicable
    }
}