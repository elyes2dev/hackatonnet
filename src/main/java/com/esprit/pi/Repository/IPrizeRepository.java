package com.esprit.pi.Repository;

import com.esprit.pi.entities.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPrizeRepository extends JpaRepository<Prize,Long> {
    List<Prize> findByHackathonId(long hackathonId);
    List<Prize> findBySponsorId(long sponsorId);
    long countBySponsorIdAndHackathonId(Long sponsorId, Long hackathonId);
    @Query("""
    SELECT p FROM Prize p
    JOIN p.sponsor s
    JOIN s.sponsorReward r
    ORDER BY r.badge DESC,
             CASE WHEN p.status = 'PENDING' THEN 0 ELSE 1 END ASC,
             p.submittedAt ASC
    """)
    List<Prize> findAllPrizesByBadgePriority();


}
