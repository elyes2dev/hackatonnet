package com.esprit.pi.services;

import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.repositories.IHackathonRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HackathonService implements IHackathonService{

    @Autowired
    IHackathonRepository hackathonRepository;
    @Override
    public Hackathon createHackathon(Hackathon hackathon) {
        return hackathonRepository.save(hackathon);
    }

    @Override
    public Hackathon updateHackathon(Long id, Hackathon hackathon) {
        return hackathonRepository.save(hackathon);
    }

    @Override
    public void deleteHackathon(Long id) {
        hackathonRepository.deleteById(id);

    }

    @Override
    public Hackathon getHackathonById(Long id) {
        return hackathonRepository.findById(id).orElse(null);
    }

    @Override
    public List<Hackathon> getAllHackathons() {
        return (List<Hackathon>) hackathonRepository.findAll();
    }
}
