package com.esprit.pi.Service;

import com.esprit.pi.DTO.SponsorInfoDTO;
import com.esprit.pi.entities.SponsorReward;
import com.esprit.pi.entities.User;

import java.util.List;

public interface ISponsorRewardService {
    void createSponsorReward(User sponsor);
    void addPoints(Long sponsorId,int points);
    void deductPoints(Long sponsorId,int points);
    SponsorReward getReward(Long sponsorId);
    List<SponsorInfoDTO> getTopSponsors();
    String getBadgeIcon(SponsorReward.SponsorBadge badge);
}
