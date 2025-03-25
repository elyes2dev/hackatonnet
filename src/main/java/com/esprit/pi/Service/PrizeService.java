package com.esprit.pi.Service;

import com.esprit.pi.DTO.SponsorInfoDTO;
import com.esprit.pi.Repository.*;
import com.esprit.pi.entities.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    @Autowired
    SponsorRewardService sponsorRewardService;
    @Autowired
    ISponsorApplicationRepository sponsorApplicationRepository;

    public Prize createPrize(long sponsorId, long hackathonId, Prize prize) {
        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));

        // Ensure the user has the SPONSOR role
        Role sponsorRole = roleRepository.findByName("SPONSOR")
                .orElseThrow(() -> new RuntimeException("Role SPONSOR not found"));

        if (!sponsor.getRoles().contains(sponsorRole)) {
            throw new RuntimeException("Only sponsors can add prizes.");
        }

        prize.setSponsor(sponsor);
        prize.setHackathon(hackathon);
        prize.setSubmittedAt(LocalDateTime.now());

        // Check sponsor badge level
        SponsorReward reward = sponsorRewardService.getReward(sponsorId);

        // ✅ Enforce the badge-based prize limit
        checkPrizeLimit(sponsor, hackathon);

        // Auto-approve if Platinum
        if (reward.getBadge() == SponsorReward.SponsorBadge.PLATINUM) {
            prize.setStatus(ApplicationStatus.APPROVED);
            prize.setReviewedAt(LocalDateTime.now());

            // ✅ Grant points immediately for Platinum auto-approval
            int points = calculatePoints(prize);
            sponsorRewardService.addPoints(sponsorId, points);
        } else {
            prize.setStatus(ApplicationStatus.PENDING);
        }

        return prizeRepository.save(prize);
    }

    public int calculatePoints(Prize prize) {
        return (prize.getPrizeType() == Prize.PrizeType.MONEY)
                ? (int) prize.getAmount().doubleValue() / 100
                : 10; // Default for PRODUCT
    }


    public List<Prize> getAllPrizes() {
        return prizeRepository.findAllPrizesByBadgePriority();
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

        // ✅ Grant points using the same logic as auto-approval
        int points = calculatePoints(prize);
        sponsorRewardService.addPoints(prize.getSponsor().getId(), points);

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

    public List<SponsorInfoDTO> getSponsorsByHackathon(Long hackathonId) {
        return prizeRepository.findByHackathonId(hackathonId).stream()
                .map(Prize::getSponsor) // Get sponsors from prizes
                .distinct() // Avoid duplicate sponsors
                .map(sponsor -> {
                    // Fetch related SponsorReward and SponsorApplication
                    SponsorReward reward = sponsorRewardService.getReward(sponsor.getId());
                    SponsorApplication application = sponsorApplicationRepository.findByUserId(sponsor.getId())
                            .orElseThrow(() -> new RuntimeException("Sponsor application not found"));

                    // Build the DTO with all sponsor data
                    return new SponsorInfoDTO(
                            sponsor.getId(),
                            sponsor.getName(),
                            sponsor.getLastname(),
                            application.getCompanyName(),
                            application.getCompanyLogo(),
                            application.getWebsiteUrl(),
                            reward.getReputationPoints(),
                            reward.getBadge(),
                            sponsorRewardService.getBadgeIcon(reward.getBadge())
                    );
                })
                .sorted(Comparator.comparing(
                        SponsorInfoDTO::getBadge, Comparator.reverseOrder())) // Sort by badge priority
                .toList();
    }


    public long countSponsorPrizesInHackathon(Long sponsorId, Long hackathonId) {
        return prizeRepository.countBySponsorIdAndHackathonId(sponsorId, hackathonId);
    }

    // Get max allowed prizes by badge
    public int getPrizeLimitByBadge(SponsorReward.SponsorBadge badge) {
        return switch (badge) {
            case BRONZE -> 1;      // Bronze: 1 prize per hackathon
            case SILVER -> 3;      // Silver: 3 prizes per hackathon
            case GOLD -> 5;        // Gold: 5 prizes per hackathon
            case PLATINUM -> Integer.MAX_VALUE; // Platinum: Unlimited prizes
        };
    }

    // Check if sponsor exceeded their prize limit
    public void checkPrizeLimit(User sponsor, Hackathon hackathon) {
        int currentPrizes = (int) countSponsorPrizesInHackathon(sponsor.getId(), hackathon.getId());
        int maxAllowed = getPrizeLimitByBadge(sponsor.getSponsorReward().getBadge());

        if (currentPrizes >= maxAllowed) {
            throw new RuntimeException("You have reached the maximum prize limit for your badge level.");
        }
    }


}
