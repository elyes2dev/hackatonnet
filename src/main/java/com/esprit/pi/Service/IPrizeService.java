package com.esprit.pi.Service;

import com.esprit.pi.DTO.SponsorInfoDTO;
import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.Prize;
import com.esprit.pi.entities.SponsorReward;
import com.esprit.pi.entities.User;

import java.util.List;

public interface IPrizeService {

    Prize createPrize(long sponsorId, long hackathonId, Prize prize);
    int calculatePoints(Prize prize);
    List<Prize> getAllPrizes();
    Prize getPrizeById(long id);
    List<Prize> getPrizesByHackathon(long hackathonId);
    Prize approvePrize(long id);
    Prize rejectPrize(long id);
    void deletePrize(long id);
    List<SponsorInfoDTO> getSponsorsByHackathon(Long hackathonId);
    long countSponsorPrizesInHackathon(Long sponsorId, Long hackathonId);
    int getPrizeLimitByBadge(SponsorReward.SponsorBadge badge);
    void checkPrizeLimit(User sponsor, Hackathon hackathon);
}
