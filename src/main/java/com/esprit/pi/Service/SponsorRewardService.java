package com.esprit.pi.Service;

import com.esprit.pi.DTO.SponsorInfoDTO;
import com.esprit.pi.Repository.ISponsorApplicationRepository;
import com.esprit.pi.Repository.ISponsorRewardRepository;
import com.esprit.pi.Repository.IUserRepository;
import com.esprit.pi.entities.SponsorApplication;
import com.esprit.pi.entities.SponsorReward;
import com.esprit.pi.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class SponsorRewardService implements ISponsorRewardService{

    @Autowired
    ISponsorRewardRepository sponsorRewardRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    ISponsorApplicationRepository sponsorApplicationRepository;
    @Override
    // Initialize reward record for a new sponsor
    public void createSponsorReward(User sponsor) {
        SponsorReward reward = new SponsorReward();
        reward.setSponsor(sponsor);
        reward.setReputationPoints(0); // Default
        reward.setBadge(SponsorReward.SponsorBadge.BRONZE);
        sponsorRewardRepository.save(reward);
    }

    @Override
    // Add points and update badge
    public void addPoints(Long sponsorId, int points) {
        SponsorReward reward = sponsorRewardRepository.findBySponsorId(sponsorId)
                .orElseThrow(() -> new RuntimeException("Reward record not found"));

        reward.setReputationPoints(reward.getReputationPoints() + points);

        // Update badge if needed
        SponsorReward.SponsorBadge newBadge = reward.calculateBadge();
        if (!reward.getBadge().equals(newBadge)) {
            reward.setBadge(newBadge);
        }

        sponsorRewardRepository.save(reward);
    }

    @Override
    // Deduct points (if a prize is withdrawn)
    public void deductPoints(Long sponsorId, int points) {
        addPoints(sponsorId, -points);
    }

    @Override
    // Get sponsor's current reward info
    public SponsorReward getReward(Long sponsorId) {
        return sponsorRewardRepository.findBySponsorId(sponsorId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));
    }

    @Override
    public List<SponsorInfoDTO> getTopSponsors() {
        return sponsorRewardRepository.findAll().stream()
                .sorted(Comparator.comparingInt(SponsorReward::getReputationPoints).reversed()) // Highest points first
                .limit(10) // Top 10 sponsors
                .map(reward -> {
                    User sponsor = reward.getSponsor();
                    SponsorApplication application = sponsorApplicationRepository.findByUserId(sponsor.getId())
                            .orElseThrow(() -> new RuntimeException("Sponsor application not found"));

                    return new SponsorInfoDTO(
                            sponsor.getId(),
                            sponsor.getName(),
                            sponsor.getLastname(),
                            application.getCompanyName(),
                            application.getCompanyLogo(),
                            application.getWebsiteUrl(),
                            reward.getReputationPoints(),
                            reward.getBadge(),
                            getBadgeIcon(reward.getBadge())
                    );
                })
                .toList();
    }

    public String getBadgeIcon(SponsorReward.SponsorBadge badge) {
        return switch (badge) {
            case BRONZE -> "🟫";     // Bronze Icon
            case SILVER -> "⚪";     // Silver Icon
            case GOLD -> "🟡";       // Gold Icon
            case PLATINUM -> "🔵";  // Platinum Icon
        };
    }

}
