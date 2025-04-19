package com.esprit.pi.services;

import com.esprit.pi.entities.Hackathon;

import java.util.List;

public interface IHackathonService {

    Hackathon getHackathonById(Long id);
    List<Hackathon> getAllHackathons();
}
