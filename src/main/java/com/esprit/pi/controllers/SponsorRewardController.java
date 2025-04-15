package com.esprit.pi.controllers;

import com.esprit.pi.dtos.SponsorInfoDTO;
import com.esprit.pi.services.ISponsorRewardService;
import com.esprit.pi.entities.SponsorReward;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sponsor-reward")
@AllArgsConstructor
public class SponsorRewardController {

    @Autowired
    ISponsorRewardService sponsorRewardService;

    // Get the top 10 sponsors (platform-wide leaderboard)
    @GetMapping("/top-sponsors")
    public ResponseEntity<List<SponsorInfoDTO>> getTopSponsors() {
        return ResponseEntity.ok(sponsorRewardService.getTopSponsors());
    }

    @GetMapping("/getreward/{sponsorId}")
    public ResponseEntity<SponsorReward> getSponsorReward(@PathVariable Long sponsorId) {
        return ResponseEntity.ok(sponsorRewardService.getReward(sponsorId));
    }
}
