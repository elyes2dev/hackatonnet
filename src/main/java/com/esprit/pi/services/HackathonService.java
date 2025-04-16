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
        // First find the existing hackathon
        Hackathon existingHackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hackathon not found with id: " + id));

        // Update the existing hackathon with new values
        existingHackathon.setTitle(hackathon.getTitle());
        existingHackathon.setLocation(hackathon.getLocation());
        existingHackathon.setLogo(hackathon.getLogo());
        existingHackathon.setMaxMembers(hackathon.getMaxMembers());
        existingHackathon.setIsOnline(hackathon.getIsOnline());
        existingHackathon.setDescription(hackathon.getDescription());
        existingHackathon.setStartDate(hackathon.getStartDate());
        existingHackathon.setEndDate(hackathon.getEndDate());

        // Don't update createdBy or createdAt
        return hackathonRepository.save(existingHackathon);
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
