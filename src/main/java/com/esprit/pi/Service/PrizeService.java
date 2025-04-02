package com.esprit.pi.Service;

import com.esprit.pi.DTO.HackathonDTO;
import com.esprit.pi.DTO.PrizeDTO;
import com.esprit.pi.DTO.SponsorInfoDTO;
import com.esprit.pi.DTO.UserDTO;
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
    @Autowired
    private ProductPriceService productPriceService;


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
        if (prize.getPrizeType() == Prize.PrizeType.MONEY) {
            return (int) prize.getAmount().doubleValue() / 100; // Calculate from money
        } else if (prize.getPrizeType() == Prize.PrizeType.PRODUCT) {
            // Fetch price from Google Custom Search API
            Double fetchedPrice = productPriceService.fetchProductPrice(prize.getProductName());
            if (fetchedPrice != null) {
                return (int) (fetchedPrice / 1); // Directly calculate points without a minimum
            }
        }
        return 10; // Default points if no price is found
    }




    public List<Prize> getAllPrizes() {
        return prizeRepository.findAllPrizesByBadgePriority();
    }


    public Prize getPrizeById(long id) {
        return prizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prize not found"));
    }

    public PrizeDTO getPrizeByIdDTO(long id) {
        Prize prize = prizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prize not found"));

        // Convert to PrizeDTO
        PrizeDTO prizeDTO = new PrizeDTO();
        prizeDTO.setId(prize.getId());
        prizeDTO.setPrizeType(prize.getPrizeType());
        prizeDTO.setAmount(prize.getAmount());
        prizeDTO.setProductName(prize.getProductName());
        prizeDTO.setProductDescription(prize.getProductDescription());
        prizeDTO.setStatus(prize.getStatus());
        prizeDTO.setSubmittedAt(prize.getSubmittedAt());
        prizeDTO.setReviewedAt(prize.getReviewedAt());
        prizeDTO.setPrizeCategory(prize.getPrizeCategory());

        // Convert and set UserDTO
        UserDTO sponsorDTO = new UserDTO();
        sponsorDTO.setId(prize.getSponsor().getId());
        sponsorDTO.setName(prize.getSponsor().getName());
        sponsorDTO.setLastname(prize.getSponsor().getLastname());
        sponsorDTO.setEmail(prize.getSponsor().getEmail());
        prizeDTO.setSponsor(sponsorDTO);

        // Convert and set HackathonDTO
        HackathonDTO hackathonDTO = new HackathonDTO();
        hackathonDTO.setId(prize.getHackathon().getId());
        hackathonDTO.setTitle(prize.getHackathon().getTitle());
        hackathonDTO.setStartDate(prize.getHackathon().getStartDate());
        hackathonDTO.setEndDate(prize.getHackathon().getEndDate());
        prizeDTO.setHackathon(hackathonDTO);

        return prizeDTO;
    }


    public List<Prize> getPrizesByHackathon(long hackathonId) {
        return prizeRepository.findByHackathonId(hackathonId);
    }

    public List<Prize> getPrizesBySponsor(Long sponsorId) {
        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        return prizeRepository.findBySponsor(sponsor);
    }

    public List<Prize> getPrizesByCategory(Prize.PrizeCategory category) {
        return prizeRepository.findByPrizeCategory(category);
    }

    public List<Prize> getPrizesBySponsorAndCategory(Long sponsorId, Prize.PrizeCategory category) {
        User sponsor = userRepository.findById(sponsorId)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
        return prizeRepository.findBySponsorAndPrizeCategory(sponsor, category);
    }

    public List<Prize> getPrizesByHackathonAndCategory(Long hackathonId, Prize.PrizeCategory category) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));
        return prizeRepository.findByHackathonAndPrizeCategory(hackathon, category);
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

    public Prize cancelPrize(long prizeId, long sponsorId) {
        Prize prize = getPrizeById(prizeId);

        // Ensure only the sponsor who created the prize can cancel it
        if (prize.getSponsor().getId() != sponsorId) {
            throw new RuntimeException("Unauthorized: You can only cancel your own prizes.");
        }

        // If the prize is already approved, deduct points and adjust badge if necessary
        if (prize.getStatus() == ApplicationStatus.APPROVED) {
            int points = calculatePoints(prize);
            sponsorRewardService.deductPoints(sponsorId, points);
        }

        // Mark the prize as CANCELED
        prize.setStatus(ApplicationStatus.CANCELED);
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
        return prizeRepository.countBySponsorIdAndHackathonIdAndStatus(sponsorId, hackathonId, ApplicationStatus.APPROVED);
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
