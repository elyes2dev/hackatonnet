package com.esprit.pi.services;

import com.esprit.pi.entities.Hackathon;

import java.util.List;

public interface IHackathonService {
    Hackathon createHackathon(Hackathon hackathon);
    Hackathon updateHackathon(Long id, Hackathon hackathon);
    void deleteHackathon(Long id);
    Hackathon getHackathonById(Long id);
    List<Hackathon> getAllHackathons();
}
