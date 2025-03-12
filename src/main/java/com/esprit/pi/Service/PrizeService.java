package com.esprit.pi.Service;

import com.esprit.pi.Repository.IHackathonRepository;
import com.esprit.pi.Repository.IPrizeRepository;
import com.esprit.pi.Repository.IRoleRepository;
import com.esprit.pi.Repository.IUserRepository;
import com.esprit.pi.entities.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PrizeService implements IPrizeService{

    @Autowired
    IPrizeRepository prizeRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IHackathonRepository hackathonRepository;
    @Autowired
    IRoleRepository roleRepository;

    public Prize createPrize(long sponsorId, long hackathonId, Prize prize) {
        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        // Fetch SPONSOR role from the DB
        Role sponsorRole = roleRepository.findByName("SPONSOR")
                .orElseThrow(() -> new RuntimeException("Role SPONSOR not found"));

        // Check if the user has the sponsor role
        if (!sponsor.getRoles().contains(sponsorRole)) {
            throw new RuntimeException("Only sponsors can add prizes.");
        }

        prize.setSponsor(sponsor);
        prize.setHackathon(hackathon);
        prize.setStatus(ApplicationStatus.PENDING);
        prize.setSubmittedAt(LocalDateTime.now());
        return prizeRepository.save(prize);
    }

    public List<Prize> getAllPrizes() {
        return prizeRepository.findAll();
    }

    public Prize getPrizeById(long id) {
        return prizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prize not found"));
    }

    public List<Prize> getPrizesByHackathon(long hackathonId) {
        return prizeRepository.findByHackathonId(hackathonId);
    }

    public Prize approvePrize(long id) {
        Prize prize = getPrizeById(id);
        prize.setStatus(ApplicationStatus.APPROVED);
        prize.setReviewedAt(LocalDateTime.now());
        return prizeRepository.save(prize);
    }

    public Prize rejectPrize(long id) {
        Prize prize = getPrizeById(id);
        prize.setStatus(ApplicationStatus.REJECTED);
        prize.setReviewedAt(LocalDateTime.now());
        return prizeRepository.save(prize);
    }

    public void deletePrize(long id) {
        prizeRepository.deleteById(id);
    }
}
