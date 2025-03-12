package com.esprit.pi.Service;

import com.esprit.pi.entities.Prize;

import java.util.List;

public interface IPrizeService {

    Prize createPrize(long sponsorId, long hackathonId, Prize prize);
    List<Prize> getAllPrizes();
    Prize getPrizeById(long id);
    List<Prize> getPrizesByHackathon(long hackathonId);
    Prize approvePrize(long id);
    Prize rejectPrize(long id);
    void deletePrize(long id);
}
