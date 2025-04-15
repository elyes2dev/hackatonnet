package com.esprit.pi.repositories;

import com.esprit.pi.entities.ApplicationStatus;
import com.esprit.pi.entities.Hackathon;
import com.esprit.pi.entities.Prize;
import com.esprit.pi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPrizeRepository extends JpaRepository<Prize,Long> {
    List<Prize> findByHackathonId(long hackathonId);
    List<Prize> findBySponsorId(long sponsorId);
    long countBySponsorIdAndHackathonIdAndStatus(Long sponsorId, Long hackathonId, ApplicationStatus status);

    @Query("""
    SELECT p FROM Prize p
    JOIN p.sponsor s
    JOIN s.sponsorReward r
    ORDER BY r.badge DESC,
             CASE WHEN p.status = 'PENDING' THEN 0 ELSE 1 END ASC,
             p.submittedAt ASC
    """)
    List<Prize> findAllPrizesByBadgePriority();

    List<Prize> findBySponsor(User sponsor);

    List<Prize> findByHackathon(Hackathon hackathon);

    List<Prize> findByPrizeCategory(Prize.PrizeCategory category);

    List<Prize> findBySponsorAndPrizeCategory(User sponsor, Prize.PrizeCategory category);

    List<Prize> findByHackathonAndPrizeCategory(Hackathon hackathon, Prize.PrizeCategory category);


}
